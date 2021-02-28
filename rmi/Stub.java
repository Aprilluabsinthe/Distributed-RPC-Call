package rmi;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Proxy;
import java.net.*;

import rmi.data.Message;
import rmi.helper.Helper;
import rmi.proxyInvocationHandler;

import static rmi.helper.Helper.allThrowRMIExceptions;

/** RMI stub factory.

    <p>
    RMI stubs hide network communication with the remote server and provide a
    simple object-like interface to their users. This class provides methods for
    creating stub objects dynamically, when given pre-defined interfaces.

    <p>
    The network address of the remote server is set when a stub is created, and
    may not be modified afterwards. Two stubs are equal if they implement the
    same interface and carry the same remote server address - and would
    therefore connect to the same skeleton. Stubs are serializable.
 */
public abstract class Stub
{
    /** Creates a stub, given a skeleton with an assigned adress.

        <p>
        The stub is assigned the address of the skeleton. The skeleton must
        either have been created with a fixed address, or else it must have
        already been started.

        <p>
        This method should be used when the stub is created together with the
        skeleton. The stub may then be transmitted over the network to enable
        communication with the skeleton.

        @param c A <code>Class</code> object representing the interface
                 implemented by the remote object.
        @param skeleton The skeleton whose network address is to be used.
        @return The stub created.
        @throws IllegalStateException If the skeleton has not been assigned an
                                      address by the user and has not yet been
                                      started.
        @throws UnknownHostException When the skeleton address is a wildcard and
                                     a port is assigned, but no address can be
                                     found for the local host.
        @throws NullPointerException If any argument is <code>null</code>.
        @throws Error If <code>c</code> does not represent a remote interface
                      - an interface in which each method is marked as throwing
                      <code>RMIException</code>, or if an object implementing
                      this interface cannot be dynamically created.
     */
    public static <T> T create(Class<T> c, Skeleton<T> skeleton)
        throws UnknownHostException
    {
        if(skeleton.getAddr() == null){
            throw new IllegalStateException("keleton has not been assigned an address by the user and has not yet been started.");
        }
        if(c == null || skeleton == null){
            throw new NullPointerException("interface or server is null");
        }
        if( !Helper.isServerInterface(c,skeleton)){
            throw new Error("interface Error: not an interface or not belongs to server");
        }
        // if every method throws RMIExeption
        if (!allThrowRMIExceptions(c)){
            throw new Error("Methods do not throw RMIException");
        }
        try{
            proxyInvocationHandler stubHandler = new proxyInvocationHandler(skeleton.getAddr());
            ClassLoader loader = skeleton.getClassT().getClassLoader();
            Class<?>[] interfaces = skeleton.getServer().getClass().getInterfaces();
            return (T) Proxy.newProxyInstance(loader,interfaces,stubHandler);
        }
        catch (Exception e){
            throw new UnsupportedOperationException("not implemented");
        }
    }

    /** Creates a stub, given a skeleton with an assigned address and a hostname
        which overrides the skeleton's hostname.

        <p>
        The stub is assigned the port of the skeleton and the given hostname.
        The skeleton must either have been started with a fixed port, or else
        it must have been started to receive a system-assigned port, for this
        method to succeed.

        <p>
        This method should be used when the stub is created together with the
        skeleton, but firewalls or private networks prevent the system from
        automatically assigning a valid externally-routable address to the
        skeleton. In this case, the creator of the stub has the option of
        obtaining an externally-routable address by other means, and specifying
        this hostname to this method.

        @param c A <code>Class</code> object representing the interface
                 implemented by the remote object.
        @param skeleton The skeleton whose port is to be used.
        @param hostname The hostname with which the stub will be created.
        @return The stub created.
        @throws IllegalStateException If the skeleton has not been assigned a
                                      port.
        @throws NullPointerException If any argument is <code>null</code>.
        @throws Error If <code>c</code> does not represent a remote interface
                      - an interface in which each method is marked as throwing
                      <code>RMIException</code>, or if an object implementing
                      this interface cannot be dynamically created.
     */
    public static <T> T create(Class<T> c, Skeleton<T> skeleton,
                               String hostname)
    {
        if((skeleton.getAddr() == null) || (skeleton.getPort() == 0)){
            throw new IllegalStateException("skeleton has not been assigned an address by the user and has not yet been started.");
        }
        if(c == null || skeleton == null){
            throw new NullPointerException("interface or server is null");
        }
        if( !Helper.isServerInterface(c,skeleton)){
            throw new Error("interface Error: not an interface or not belongs to server");
        }
        // if every method throws RMIExeption
        if (!allThrowRMIExceptions(c)){
            throw new Error("Methods do not throw RMIException");
        }
        try{
            InetSocketAddress newaddr = InetSocketAddress.createUnresolved(hostname,skeleton.getAddr().getPort());
            skeleton.setAddr(newaddr);
            proxyInvocationHandler stubHandler = new proxyInvocationHandler(skeleton.getAddr());
            ClassLoader loader = skeleton.getClassT().getClassLoader();
            Class<?>[] interfaces = skeleton.getServer().getClass().getInterfaces();
            return (T) Proxy.newProxyInstance(loader,interfaces,stubHandler);
        }
        catch (Exception e) {
            throw new UnsupportedOperationException("not implemented");
        }
    }

    /** Creates a stub, given the address of a remote server.

        <p>
        This method should be used primarily when bootstrapping RMI. In this
        case, the server is already running on a remote host but there is
        not necessarily a direct way to obtain an associated stub.

        @param c A <code>Class</code> object representing the interface
                 implemented by the remote object.
        @param address The network address of the remote skeleton.
        @return The stub created.
        @throws NullPointerException If any argument is <code>null</code>.
        @throws Error If <code>c</code> does not represent a remote interface
                      - an interface in which each method is marked as throwing
                      <code>RMIException</code>, or if an object implementing
                      this interface cannot be dynamically created.
     */
    public static <T> T create(Class<T> c, InetSocketAddress address)
    {
        if(c == null || address == null){
            throw new NullPointerException("interface or server is null");
        }
        // if every method throws RMIExeption
        if (!allThrowRMIExceptions(c)){
            throw new Error("Methods do not throw RMIException");
        }

        // no skeleton for use, apply for a skeleton
        Skeleton<T> skeleton = null;
        try{
            Socket socket = new Socket(address.getHostName(), address.getPort());
            ObjectOutputStream outstream = new ObjectOutputStream(socket.getOutputStream());

            // new Message, requesting for a skeleton
            Message<Skeleton<T>> sklReqMsg = new Message<Skeleton<T>>(null, Helper.MessageType.SkeletonRequest);
            outstream.writeObject(sklReqMsg);
            outstream.flush();

            // new InputStream Object
            ObjectInputStream instream = new ObjectInputStream(socket.getInputStream());
            Message<?> sklResMsg = (Message<?>) instream.readObject();
            boolean received = false;
            while(!received){
                if(sklResMsg == null){ // do nothing
                }
                else{// received something
                    received = true;
                    if(Helper.checkDataType(sklResMsg, Helper.MessageType.SkeletonResponse)) {
                        skeleton = (Skeleton<T>) sklResMsg.getData();
                    }
                    else{
                        throw new Error("Expecting SkeletonResponse, received unexpected Message");
                    }
                }
            }

            if(skeleton == null){
                throw new Error("Expecting SkeletonResponse, received unexpected Message");
            }
            if( !Helper.isServerInterface(c,skeleton)){
                throw new Error("interface Error: not an interface or not belongs to server");
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try{
            proxyInvocationHandler stubHandler = new proxyInvocationHandler(address);
            ClassLoader loader = skeleton.getClassT().getClassLoader();
            Class<?>[] interfaces = skeleton.getServer().getClass().getInterfaces();
            return (T) Proxy.newProxyInstance(loader,interfaces,stubHandler);
        }
        catch (Exception e) {
            throw new UnsupportedOperationException("not implemented");
        }
    }
}
