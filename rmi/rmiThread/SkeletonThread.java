package rmi.rmiThread;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import rmi.Skeleton;

/**
    ref: https://docs.oracle.com/javase/tutorial/essential/concurrency/locksync.html
 https://web.mit.edu/6.005/www/fa15/classes/23-locks/
 https://www.geeksforgeeks.org/introducing-threads-socket-programming-java/
*/
public class SkeletonThread<T> extends Thread implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private ServerSocket ss;
    private int port;
    private InetSocketAddress address;
    private volatile boolean isRunning = false;
    private Skeleton<T> skeleton;
    private final Object lock = new Object();
    private final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);

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
        Socket clientSocket = null;
        try {
            ss = new ServerSocket(port);
            System.out.println("Skeleton Thread Waiting for Client");
            while (true) {
                synchronized(this.lock){
                    try{
                        clientSocket = ss.accept();
                        Thread task = new ClientHandler<T>(skeleton,clientSocket);
                        clientProcessingPool.submit(task);
                        task.start();
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


}
