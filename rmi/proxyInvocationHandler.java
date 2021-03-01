package rmi;

import rmi.helper.Helper;
import rmi.helper.Helper.*;
import rmi.data.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Processes a method invocation on a proxy instance and returns the result.
 * This method will be invoked on an invocation handler when a method is invoked on a proxy instance that it is associated with.
  */
public class proxyInvocationHandler<T> implements InvocationHandler,Serializable {
    private static final long serialVersionUID = 3141566360570749149L;
    private final InetSocketAddress socketAddress;
    private final String hostname;
    private final int port;
    private Class<T> clazz;
    private Skeleton<T> skeleton;

    public proxyInvocationHandler(Class<T> clazz, Skeleton<T> skeleton, InetSocketAddress socketAddress){
        this.clazz = clazz;
        this.skeleton = skeleton;
        this.socketAddress = socketAddress;
        this.hostname = socketAddress.getHostName();
        this.port = socketAddress.getPort();
    }

    public proxyInvocationHandler(Class<T> clazz, Skeleton<T> skeleton, String hostname, int port){
        this.clazz = clazz;
        this.skeleton = skeleton;
        this.hostname = hostname;
        this.port = port;
        this.socketAddress = InetSocketAddress.createUnresolved(hostname,port);
    }

    /**
     * Construction with interfaces <code>c</code> and <code>skeleton</code>
     * @param clazz the interfaces c
     * @param skeleton the skeleton to be used
     */
    public proxyInvocationHandler(Class<T> clazz, Skeleton<T> skeleton){
        this.clazz = clazz;
        this.skeleton = skeleton;
        this.hostname = skeleton.getHostName();
        this.port = skeleton.getPort();
        this.socketAddress = InetSocketAddress.createUnresolved(hostname,port);
    }

    /**
     * invoke on the current object
     * <p>
     *     <li>Override the equals/toString/HashCode for abstract Class Factory Stub</li>
     *     <li>implement the proxy handler for Stub, write outputstream into Message and read from inputstream Message</li>
     *
     * </p>
     *
     * @param proxy a Proxy instance
     * @param method the method to be called by Client Stub
     * @param args arguments Objects for the method
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        /** override equals, toString and hashCode
         * The stub is abstract with no instance
         * thus using the Reflect class
         * if method called euqals "equals"/"hashCode"/"toString", override in invoke
        * */
        if(method.equals( Object.class.getMethod("equals"))){
            if (!(args[0] instanceof Proxy)) return false;
            else{
                proxyInvocationHandler other = (proxyInvocationHandler) Proxy.getInvocationHandler(args[0]);
                return (clazz.equals( other.getClazz() ) && skeleton.equals( other.getSkeleton()));
            }
        }

        if(method.equals( Object.class.getMethod("hashCode") )){
            return skeleton.hashCode() * 31 + clazz.hashCode()  * 31 ;
        }

        if(method.equals( Object.class.getMethod("toString") )){
            return "Class Interface=" + clazz.getCanonicalName()+
                    "remote address hostname=" + socketAddress.getHostName() +
                    "remote address port=" + socketAddress.getPort();
        }

        /** The body of invoke handler
         * */
        Object invokeResult = null;
        try{
            Socket socket = new Socket(this.hostname,this.port);
            MethodRequestMessageData mrmd = new MethodRequestMessageData(method.getName(), method.getParameterTypes(), args);
            // new OutputStream Object, apply for a new request Message and write into message
            Message<MethodRequestMessageData> message = new Message<>(mrmd, MessageType.MethodRequest);
            ObjectOutputStream outstream = new ObjectOutputStream(socket.getOutputStream());
            outstream.writeObject(message);
            outstream.flush();

            // new InputStream Object, apply for a new new response Message and read from instream
            ObjectInputStream instream = new ObjectInputStream(socket.getInputStream());
            Message<?> responseMsg = (Message<?>)instream.readObject();

            boolean received = false;
            while(!received){
                if( responseMsg == null){ // not received, do nothing
                }
                else{ // received something, validate and extract dtaa
                    received = true;
                    if( Helper.checkDataType(responseMsg,MessageType.MethodResponse)){
                        invokeResult= responseMsg.getData();
                    }
                    else{
                        throw new RMIException("Expecting MethodRequest, handler received invalid message");
                    }
                }
            }
            if(invokeResult == null){
                throw new RMIException("Stub Invocation generate null");
            }
            // stram object and socket close
            instream.close();
            outstream.close();
            socket.close();
        } catch (IOException | ClassNotFoundException | RMIException e) {
            e.printStackTrace();
        }
        return invokeResult;
    }

    /**
     * Getter for <code>skeleton</code>
     * @return skeleton in use
     */
    public Skeleton<T> getSkeleton() {
        return skeleton;
    }


    /**
     * Getter for interfaces <code>c</code>
     * @return c in use
     */
    public Class<T> getClazz() {
        return clazz;
    }

}

