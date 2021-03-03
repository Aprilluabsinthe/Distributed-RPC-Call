package rmi;

import rmi.helper.Helper;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

/**
 * Processes a method invocation on a proxy instance and returns the result.
 * This method will be invoked on an invocation handler when a method is invoked on a proxy instance that it is associated with.
 */
public class proxyInvocationHandler<T> implements InvocationHandler, Serializable {
    private static final long serialVersionUID = 7198949874328216107L;
    private InetSocketAddress address;
    private Class<T> clazz;

    /**
     * Constrcution Function
     * @param clazz class 
     * @param address address
     */
    public proxyInvocationHandler(Class clazz, InetSocketAddress address ) {
        this.address = address;
        this.clazz = clazz;
    }

    /**
     * Getter for Address
     * @return the address
     */
    public InetSocketAddress getAddress() {
        return address;
    }

    /**
     * Getter for class
     * in distinguish to <code>Class</code>
     * @return
     */
    public Class getClazz() {
        return clazz;
    }

    /**
     * Getter for Hostname
     * @return the host name
     */
    public String getHostName(){
        return address.getHostName();
    }

    /**
     * Getter for Port
     * @return the port
     */
    public int getPort(){
        return address.getPort();
    }

    /**
     * Overide invoke for Stub proxy
     * @param proxy the proxy object
     * @param method the method invoked
     * @param args the method's arguments
     * @return invoked result
     * @throws Throwable throw exceptions
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // override equals/hashcode/tostring in stub
        Object override = Helper.overrideStub(this,method, args);
        if(override!= null){
            return override;
        }

        else{
            try {

                    Socket socket = new Socket(getHostName(), getPort());
                    ObjectOutputStream outstream = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream instream = new ObjectInputStream(socket.getInputStream());

                    Class parameterTypes[] = method.getParameterTypes();
                    Object[] objects = new Object[]{method.getName(), args, parameterTypes};

                    outstream.writeObject(objects);
                    outstream.flush();

//                Message message =  new Message(objects,);

                    // Check if method was run successfully
                    Object data = instream.readObject();
                    Object InvokeResult = null;
                try {
                    /**
                     * Check if the instram DataStatus is Valid
                     * if not,  close all strema and socket
                     */
                    if (data.equals(Helper.DataStatus.INVALID)) {
                        Object error = instream.readObject();
                        instream.close();
                        outstream.close();
                        socket.close();
                        throw (Exception) error;
                    }

                    /**
                     * if the method is a void return method, will have no return value for InvokeResult
                     */
                    if (!method.getReturnType().equals(Void.TYPE)) {
                        InvokeResult = instream.readObject();
                    }

                }finally{
                    instream.close();
                    outstream.close();
                    socket.close();
                }
                return InvokeResult;
            } catch (Exception e) {
                /**
                 * Whether the method supports Exception e throw out
                 */
                if (Helper.methodContainsE(method,e)){
                    throw e;
                }
                throw new RMIException(e);
            }
        }
    }
}
