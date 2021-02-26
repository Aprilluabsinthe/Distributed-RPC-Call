package rmi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;

public class InvocationHandler<T> implements java.lang.reflect.InvocationHandler, Serializable {
    private static final long serialVersionUID = 3141566360570749149L;
    private InetSocketAddress address;

    public InvocationHandler(String address, int port){
        this.address = InetSocketAddress.createUnresolved(address,port);
    }

    public InvocationHandler(InetSocketAddress address){
        this.address = address;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object invokeResult = null;
        try{
            Socket socket = new Socket(this.address.getHostName(), this.address.getPort());
            ObjectOutputStream outstream = new ObjectOutputStream(socket.getOutputStream());
            // new InputStream Object
            ObjectInputStream instream = new ObjectInputStream(socket.getInputStream());
            outstream.writeObject(method);
            outstream.writeObject(args);
            outstream.flush();
            invokeResult = instream.readObject();

            // close
            instream.close();
            outstream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return invokeResult;
    }
}
