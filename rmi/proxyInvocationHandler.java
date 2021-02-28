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
import java.net.InetSocketAddress;
import java.net.Socket;

public class proxyInvocationHandler<T> implements InvocationHandler,Serializable {
    private static final long serialVersionUID = 3141566360570749149L;
    private InetSocketAddress socketAddress;
    private String hostname;
    private int port;

    public proxyInvocationHandler(InetSocketAddress socketAddress){
        this.socketAddress = socketAddress;
        this.hostname = socketAddress.getHostName();
        this.port = socketAddress.getPort();
    }

    public proxyInvocationHandler(String hostname, int port){
        this.hostname = hostname;
        this.port = port;
        this.socketAddress = InetSocketAddress.createUnresolved(hostname,port);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object invokeResult = null;
        try{
            Socket socket = new Socket(this.hostname,this.port);
            MethodRequestMessageData mrmd = new MethodRequestMessageData(method.getName(), method.getParameterTypes(), args);
            Message<MethodRequestMessageData> message = new Message<MethodRequestMessageData>(mrmd, MessageType.MethodRequest);
            ObjectOutputStream outstream = new ObjectOutputStream(socket.getOutputStream());
            outstream.writeObject(message);
            outstream.flush();

            // new InputStream Object
            ObjectInputStream instream = new ObjectInputStream(socket.getInputStream());
            // new response Message and read from instream
            Message<?> responseMsg = (Message<?>)instream.readObject();

            boolean received = false;
            while(!received){
                if( responseMsg == null){ // not received, do nothing
                }
                else{ // received something, validate and extract dtaa
                    received = true;
                    if( Helper.checkDataType(responseMsg,MessageType.MethodRequest)){
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
            instream.close();
            outstream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (RMIException e) {
            e.printStackTrace();
        }
        return invokeResult;
    }
}

