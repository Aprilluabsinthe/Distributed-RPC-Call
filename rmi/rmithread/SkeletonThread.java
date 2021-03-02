package rmi.rmithread;

import rmi.Skeleton;
import rmi.Stub;
import rmi.helper.Helper;
import rmi.helper.Helper.ThreadState;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class SkeletonThread<T> extends Thread {
    private Skeleton<T> skeleton;
    private Socket socket;
    private volatile boolean isRunning = true;

    private ThreadState threadState = ThreadState.RUNNABLE;

    public SkeletonThread(Skeleton<T> skeleton, Socket socket) {
        this.skeleton = skeleton;
        this.socket = socket;
        skeleton.addThread(this);
    }

    @Override
    public void run() {
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();

            in = new ObjectInputStream(socket.getInputStream());
            Object[] objects = (Object[]) in.readObject();
            String methodName = (String) objects[0];
            Object[] args = (Object[]) objects[1];
            Class params[] = (Class[]) objects[2];
            Method method = null;
            method = skeleton.getClassT().getMethod(methodName, params);
            Object result = null;

            try {
                result = method.invoke(skeleton.getServer(), args);
                out.writeObject(Helper.DataStatus.VALID);
                Class returnType = method.getReturnType();
                if (!returnType.equals(Void.TYPE)) {
                    if (!Helper.allThrowRMIExceptions(returnType)) {
                        out.writeObject(result);
                    } else {
                        Skeleton newSkeleton = new Skeleton(returnType, result);
                        newSkeleton.start();
                        out.writeObject(Stub.create(returnType, newSkeleton.getAddr()));
                    }
                }
            } catch (InvocationTargetException e) {
                out.writeObject(Helper.DataStatus.INVALID);
                out.writeObject(e.getTargetException());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            skeleton.removeThread(this);
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
                if (in != null) in.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ThreadState getThreadState() {
        return threadState;
    }

    public void setThreadState(ThreadState threadState) {
        this.threadState = threadState;
    }

    public Socket getSocket() {
        return socket;
    }
}
