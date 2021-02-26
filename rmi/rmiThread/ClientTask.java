package rmi.rmithread;

import rmi.Skeleton;
import rmi.helper.Helper.MessageType;
import rmi.data.Message;
import rmi.data.MethodRequestMessageData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;


/**
 * 
 * This class abstracts a client thread.
 * Ref:
 * https://docs.oracle.com/javase/7/docs/api/java/lang/Class.html
 * https://docs.oracle.com/javase/7/docs/api/java/lang/reflect/Method.html
 * 
 * @author Di Lu, Yuan Gu
 */

public class ClientTask<T> extends Thread{
    private Socket socket = null;
    private Skeleton<T> skeleton;
    private SocketAddress address;
    private boolean isRunning = true;

    public ClientTask(Skeleton<T> skeleton, Socket socket){
        try{
            this.address = socket.getRemoteSocketAddress();
            this.skeleton = skeleton;
            this.socket = socket;
        }catch(Exception e){
            System.err.println("Construct Error");
            e.printStackTrace();
        }
    }

    public ClientTask(SocketAddress address, int port){
        try {
            this.address = address;
            socket = new Socket(String.valueOf(address), port);
            System.out.println("Connected");
        }
        catch(IOException e){
            System.err.println("IO Error");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        ObjectInputStream instream = null;
        ObjectOutputStream outstream = null;

        Message requestMsg = null;
        Message responseMsg = null;
        
        while (isRunning) {
            try {
                instream = new ObjectInputStream(socket.getInputStream());

                requestMsg = (Message<?>)instream.readObject();
                MessageType requestType = requestMsg.getType();

                if (requestType == MessageType.SkeletonRequest) {

                    responseMsg = new Message<Object>(new Skeleton<T>(skeleton.getClass(), skeleton.getServer()), MessageType.SkeletonResponse);

                } else if (requestType == Message.MethodRequest) {
                    MethodRequestMessageData mrmd = (MethodRequestMessageData)requestMsg.getData();

                    /*
                    NoSuchMethodException
                    SecurityException
                    IllegalAccessException,
                    IllegalArgumentException,
                    InvocationTargetException
                    ClassNotFoundException
                    */

                    Object invokedResult = null;
                    
                    try {
                        Class<T> serverClass = Class.forName(skeleton.getClass().getName());
                        Method calledMethod = serverClass.getDeclaredMethod(mrmd.getMethodName(), mrmd.getParameterTypes());

                        // invoke the same method on the remote server
                        invokedResult = calledMethod.invoke(skeleton.getServer(), mrmd.getVarArgs());

                    } catch (NoSuchMethodException nsme) {
                        nsme.printStackTrace();
                    } catch (SecurityException se) {
                        se.printStackTrace();
                    } catch (IllegalAccessException iae) {
                        iae.printStackTrace();
                    } catch (IllegalArgumentException iae2) {
                        iae2.printStackTrace();
                    } catch (InvocationTargetException ive) {
                        ive.printStackTrace();
                    } catch (ClassNotFoundException rrr) {
                        rrr.printStackTrace();
                    }

                    responseMsg = new Message<MethodRequestMessageData>(invokedResult, MessageType.MethodResponse);

                } else {
                    responseMsg = new Message<Object>(new RMIException("invalid request"), MessageType.UnexceptedRequest);
                }

                outstream = new ObjectOutputStream(socket.getOutputStream());
                outstream.writeObject(responseMsg);
                outstream.flush();

            } catch (IOxception ioe) {
                ioe.printStackTrace();
            } catch (ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
            }
        }
    }


    public void stopThread() {
        isRunning = false;
    }
}
