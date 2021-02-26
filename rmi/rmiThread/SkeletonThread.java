package rmi.rmiThread;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

import rmi.Skeleton;

/**
    ref: https://docs.oracle.com/javase/tutorial/essential/concurrency/locksync.html
 https://web.mit.edu/6.005/www/fa15/classes/23-locks/
 https://www.geeksforgeeks.org/introducing-threads-socket-programming-java/
*/
public class SkeletonThread<T> extends Thread implements Serializable {
    @Serial
    private static final long serialVersionUID = 4890265821034549516L;
    private ServerSocket ss;
    private int port;
    private InetSocketAddress address;
    private Skeleton<T> skeleton;
    private final Object lock = new Object();
    Map<SocketAddress, ClientTask<T>> taskRegister = new HashMap<>();
    private ObjectOutputStream objout = null;
    private ObjectInputStream objin = null;

    /**
     * two constructive functions
     * the first one with initial server address, for <code>public Skeleton(Class<T> c, T server)<code>
     *
    * */
    public SkeletonThread(Skeleton<T> skeleton, InetSocketAddress address){
        this.skeleton = skeleton;
        this.address = address;
        this.port = this.address.getPort();
    }

    /**
     * two constructive functions
     * <p>
     * the second one without initial server address, for <code>public Skeleton(Class<T> c, T server, InetSocketAddress address)<code>
     *
     * */
    public SkeletonThread(Skeleton<T> skeleton){
        this.skeleton = skeleton;
        this.address = skeleton.getAddress();
        this.port = this.address.getPort();
    }

    /**
     * two constructive functions
     * <p>
     * the second one without initial server address, for <code>public Skeleton(Class<T> c, T server, InetSocketAddress address)<code>
     *
     * */
    @Override
    public void run(){
        System.out.println("Run Server side Thread");
        Socket clientSocket = null;
        try {
            ss = new ServerSocket(port);
            System.out.println("Skeleton Thread Waiting for Client");
            while (true) {
                synchronized(this.lock){
                    try{
                        clientSocket = ss.accept();
                        ClientTask<T> task = new ClientTask<T>(skeleton,clientSocket);
                        taskRegister.put(clientSocket.getRemoteSocketAddress(), task);
                    }catch(Exception e){
                        System.err.println("Server side Socket error...");
                        e.printStackTrace();
                    }
                }
            }
        }catch (IOException e){
                System.err.println("Unable to connect to Client...");
                e.printStackTrace();
        }
    }

    public void stopThread(){
        System.out.println("Stop Server side Thread");
        try{
            ss.close();
        } catch (IOException e) {
            System.out.println("Stop Server side Thread IOError");
            e.printStackTrace();
        }
        taskRegister.clear();
        System.out.println("Stop successfully");
    }

    public void clearRegister(SocketAddress address){
        try {
            if (taskRegister.containsKey(address)) {
                taskRegister.remove(address);
            }
        }catch (Exception e){
            System.err.println("clearRegister error...");
            e.printStackTrace();
        }
    }

}
