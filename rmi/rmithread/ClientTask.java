package rmi.rmithread;

import rmi.Skeleton;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import rmi.helper.Helper.*;
import rmi.helper.Helper.ThreadState;
/**
 * The Client Task, Try to connect to the serversocket
 * Everytime a new Task comes, it will apply for a new SkeletonThread
 * @author Di Lu, Yuan Gu
 */
public class ClientTask<T> extends Thread
{
    private ServerSocket serversocket;
    private Skeleton<T> skeleton;
    private volatile ThreadState tState = ThreadState.RUNNING;


    /**
     * Construction Function
     * @param serversocket
     * @param skeleton
     */
    public ClientTask(ServerSocket serversocket, Skeleton<T> skeleton){
        this.serversocket = serversocket;
        this.skeleton = skeleton;
    }

    /**
     * Override run() for ClientTask
     * It mainly apply for a new SkeletonThread and start it
     */
    @Override
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

    /**
     * whether the task is stopped or not
     * @return
     */
    public boolean isStopped() {
        return tState == ThreadState.STOPPED;
    }

    /**
     * The Setter for ThreadStatus
     * @return
     */
    public void setThreadStatus(ThreadState ts) {
        this.tState = ts;
    }

    /**
     * The Getter for ThreadState
     * @return
     */
    public ThreadState getThreadState() {
        return tState;
    }
}
