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
     * @param clazz
     * @param address
     */
    public proxyInvocationHandler(Class clazz, InetSocketAddress address ) {
        this.address = address;
        this.clazz = clazz;
    }

    /**
     * Getter for Address
     * @return
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
     * @return
     */
    public String getHostName(){
        return address.getHostName();
    }

    /**
     * Getter for Port
     * @return
     */
    public int getPort(){
        return address.getPort();
    }

    /**
     * Overide invoke for Stub proxy
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
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

//                if(checkDataType())
                if (data.equals(Helper.DataStatus.INVALID)) {
                    Object error = instream.readObject();
                    instream.close();
                    outstream.close();
                    socket.close();
                    throw (Exception) error;
                }

                if (!method.getReturnType().equals(Void.TYPE)) {
                    InvokeResult = instream.readObject();
                }
                instream.close();
                outstream.close();
                socket.close();
                return InvokeResult;
            } catch (Exception e) {
                if (Helper.methodContainsE(method,e)){
                    throw e;
                }
                throw new RMIException(e);
            }
        }
    }
}
