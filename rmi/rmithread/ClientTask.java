package rmi.rmithread;

import rmi.Skeleton;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import rmi.helper.Helper.*;
import rmi.helper.Helper.ThreadState;
/**
 * The Client Task, Try to connect to the serversocket
 * @param <T>
 */
public class ClientTask<T> extends Thread
{
    private ServerSocket serversocket;
    private Skeleton<T> skeleton;

//    private volatile boolean stopped = false;

    private volatile ThreadState tState = ThreadState.RUNNING;


    public ClientTask(ServerSocket serversocket, Skeleton<T> skeleton){
        this.serversocket = serversocket;
        this.skeleton = skeleton;
    }

    public void run() {
        while ( tState == ThreadState.RUNNING ) {
            try {
                Socket ss = serversocket.accept();
                SkeletonThread newSkeleton = new SkeletonThread<>(skeleton,ss);
                newSkeleton.start();
            } catch (IOException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public boolean isStopped() {
        return tState == ThreadState.STOPPED;
    }

    public void setThreadStatus(ThreadState ts) {
        this.tState = ts;
    }

    public ThreadState getThreadState() {
        return tState;
    }
}
