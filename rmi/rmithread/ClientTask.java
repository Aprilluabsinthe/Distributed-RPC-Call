package rmi.rmithread;

import rmi.Skeleton;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientTask<T> extends Thread
{
    private ServerSocket serversocket;
    private Skeleton<T> skeleton;
    private volatile boolean stopped = false;

    public ClientTask(ServerSocket serversocket, Skeleton<T> skeleton){
        this.serversocket = serversocket;
        this.skeleton = skeleton;
    }

    public void run() {
        while (!stopped) {
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
        return stopped;
    }

    public void setStopped(boolean b) {
        this.stopped = b;
    }
}
