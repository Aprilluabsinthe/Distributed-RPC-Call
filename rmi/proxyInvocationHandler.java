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

    public proxyInvocationHandler(Class clazz, InetSocketAddress address ) {
        this.address = address;
        this.clazz = clazz;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public Class getClazz() {
        return clazz;
    }

    public String getHostName(){
        return address.getHostName();
    }

    public int getPort(){
        return address.getPort();
    }

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
                if (Arrays.asList(method.getExceptionTypes()).contains(e.getClass())) throw e;
                throw new RMIException(e);
            }
        }
    }
}
