package rmi;

import rmi.helper.Helper;
import rmi.rmithread.ClientTask;
import rmi.rmithread.SkeletonThread;
import rmi.rmithread.threadRegister;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Objects;

/** RMI skeleton

 <p>
 A skeleton encapsulates a multithreaded TCP server. The server's clients are
 intended to be RMI stubs created using the <code>Stub</code> class.

 <p>
 The skeleton class is parametrized by a type variable. This type variable
 should be instantiated with an interface. The skeleton will accept from the
 stub requests for calls to the methods of this interface. It will then
 forward those requests to an object. The object is specified when the
 skeleton is constructed, and must implement the remote interface. Each
 method in the interface should be marked as throwing
 <code>RMIException</code>, in addition to any other exceptions that the user
 desires.

 <p>
 Exceptions may occur at the top level in the listening and service threads.
 The skeleton's response to these exceptions can be customized by deriving
 a class from <code>Skeleton</code> and overriding <code>listen_error</code>
 or <code>service_error</code>.
 */
public class Skeleton<T>
{
    private Class<T> c;
    private T server;
    private InetSocketAddress address;
    private ClientTask clientTask;
    private ServerSocket listener;
    private threadRegister<T> threadRegister = new threadRegister();

    /** Creates a <code>Skeleton</code> with no initial server address. The
     address will be determined by the system when <code>start</code> is
     called. Equivalent to using <code>Skeleton(null)</code>.

     <p>
     This constructor is for skeletons that will not be used for
     bootstrapping RMI - those that therefore do not require a well-known
     port.

     @param c An object representing the class of the interface for which the
     skeleton server is to handle method call requests.
     @param server An object implementing said interface. Requests for method
     calls are forwarded by the skeleton to this object.
     @throws Error If <code>c</code> does not represent a remote interface -
     an interface whose methods are all marked as throwing
     <code>RMIException</code>.
     @throws NullPointerException If either of <code>c</code> or
     <code>server</code> is <code>null</code>.
     */

    public Skeleton(Class<T> c, T server)
    {
        if(c == null || server == null){
            throw new NullPointerException("interface or server is null");
        }
        if( !c.isInterface() ){
            throw new Error("class is not an interface");
        }
        // if every method throws RMIExeption
        if (!Helper.allThrowRMIExceptions(c)) {
            throw new Error("the interface is not remote a interface");
        }
        this.c = c;
        this.server = server;
    }

    /** Creates a <code>Skeleton</code> with the given initial server address.

     <p>
     This constructor should be used when the port number is significant.

     @param c An object representing the class of the interface for which the
     skeleton server is to handle method call requests.
     @param server An object implementing said interface. Requests for method
     calls are forwarded by the skeleton to this object.
     @param address The address at which the skeleton is to run. If
     <code>null</code>, the address will be chosen by the
     system when <code>start</code> is called.
     @throws Error If <code>c</code> does not represent a remote interface -
     an interface whose methods are all marked as throwing
     <code>RMIException</code>.
     @throws NullPointerException If either of <code>c</code> or
     <code>server</code> is <code>null</code>.
     */
    public Skeleton(Class<T> c, T server, InetSocketAddress address)
    {
        if(c == null || server == null || address == null){
            throw new NullPointerException("interface or server is null");
        }
        if( !c.isInterface() ){
            throw new Error("class is not an interface");
        }
        // if every method throws RMIExeption
        if (!Helper.allThrowRMIExceptions(c)) {
            throw new Error("the interface is not remote a interface");
        }
        this.c = c;
        this.server = server;
        this.address = address;
    }


    /** Called when the listening thread exits.

     <p>
     The listening thread may exit due to a top-level exception, or due to a
     call to <code>stop</code>.

     <p>
     When this method is called, the calling thread owns the lock on the
     <code>Skeleton</code> object. Care must be taken to avoid deadlocks when
     calling <code>start</code> or <code>stop</code> from different threads
     during this call.

     <p>
     The default implementation does nothing.

     @param cause The exception that stopped the skeleton, or
     <code>null</code> if the skeleton stopped normally.
     */
    protected void stopped(Throwable cause)
    {
        while (!threadRegister.isEmpty()) {
            System.out.println("have other Threads to Execute..");
            try {
                Thread t = null;
                synchronized (threadRegister) {
                    t = threadRegister.next();
                    if(t == null) {
                        return;
                    }
                    else{
                        t.join();
                    }
                }
            } catch (InterruptedException e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /** Called when an exception occurs at the top level in the listening
     thread.

     <p>
     The intent of this method is to allow the user to report exceptions in
     the listening thread to another thread, by a mechanism of the user's
     choosing. The user may also ignore the exceptions. The default
     implementation simply stops the server. The user should not use this
     method to stop the skeleton. The exception will again be provided as the
     argument to <code>stopped</code>, which will be called later.

     @param exception The exception that occurred.
     @return <code>true</code> if the server is to resume accepting
     connections, <code>false</code> if the server is to shut down.
     */
    protected boolean listen_error(Exception exception){return false;}

    /** Called when an exception occurs at the top level in a service thread.

     <p>
     The default implementation does nothing.

     @param exception The exception that occurred.
     */
    protected void service_error(RMIException exception)
    {
    }

    /** Starts the skeleton server.

     <p>
     A thread is created to listen for connection requests, and the method
     returns immediately. Additional threads are created when connections are
     accepted. The network address used for the server is determined by which
     constructor was used to create the <code>Skeleton</code> object.

     @throws RMIException When the listening socket cannot be created or
     bound, when the listening thread cannot be created,
     or when the server has already been started and has
     not since stopped.
     */
    public synchronized void start() throws RMIException {
        if (clientTask !=null && clientTask.isAlive()){
            throw new RMIException("Listening server is already running!\n");
        }

        if (address != null) {
            try {
                listener = new ServerSocket(address.getPort());
            } catch (IOException e) {
                throw new RMIException("Cannot create listening socket!\n");
            }
        }
        else{ // local dummy skeleton
            try{
                listener = new ServerSocket(0);
                String localhost = InetAddress.getLocalHost().getHostName();
                int localPort = listener.getLocalPort();
                address = InetSocketAddress.createUnresolved(localhost,localPort);
            }catch(UnknownHostException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // If listener is still null:
        if (listener ==null) throw new RMIException("Cannot create listening socket!\n");

        // start client Task
        clientTask = new ClientTask(listener,this);
        clientTask.start();

    }

    /** Stops the skeleton server, if it is already running.

     <p>
     The listening thread terminates. Threads created to service connections
     may continue running until their invocations of the <code>service</code>
     method return. The server stops at some later time; the method
     <code>stopped</code> is called at that point. The server may then be
     restarted.
     */
    public synchronized void stop()  {
        System.out.println("call stop!");
        if (clientTask.isAlive()) {
            clientTask.setStopped(true);
        }

        try {
            listener.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stopped(null);

    }

    /**
     * Getter for InetSocketAddress Address
     * @return address
     */
    public InetSocketAddress getAddr(){
        return this.address;
    }

    /**
     * Setter for InetSocketAddress Address
     * @param address
     */
    public void setAddr(InetSocketAddress address) {
        this.address = address;
    }
    /**
     * Getter for port in use
     * @return port
     */
    public int getPort() {
        return this.address.getPort();
    }

    /**
     * Setter for getHostName in use
     * @return HostName
     */
    public String getHostName() {
        return this.address.getHostName();
    }

    /**
     * Getter for interfaces <code>c</code> in use
     * Different from getClass() method
     * @return <code>c</code>
     */
    public Class<T> getClassT() {
        return c;
    }


    /**
     * Setter for interfaces <code>c</code>
     * @param claz
     */
    public void setClass(Class<T> claz) {
        this.c = claz;
    }

    /**
     * Getter for Server
     * @return server
     */
    public T getServer() {
        return server;
    }

    /**
     * Setter for server
     * @param server
     */
    public void setServer(T server) {
        this.server = server;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Skeleton)) return false;
        Skeleton<?> skeleton = (Skeleton<?>) o;
        return Objects.equals(address, skeleton.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }

    /**
     * Methods to maintain a Thread Set
     * Remove task from set
     * @param tSkeletonThread
     */
    public void removeThread(SkeletonThread<T> tSkeletonThread) {
        threadRegister.removeClientTask(tSkeletonThread.getSocket());
    }

    /**
     * Methods to maintain a Thread Set
     * add task to set
     * @param tSkeletonThread
     */
    public void addThread(SkeletonThread<T> tSkeletonThread) {
        threadRegister.addClientTask(tSkeletonThread.getSocket(),tSkeletonThread);
    }
}
