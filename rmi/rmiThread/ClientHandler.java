package rmi.rmiThread;

import rmi.Skeleton;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ClientHandler<T> extends Thread{
    private Socket socket = null;
    private Skeleton<T> skeleton;
    private ObjectInputStream instream = null;
    private ObjectOutputStream outstream = null;

    public ClientHandler(Skeleton<T> skeleton, Socket socket){
        try{
            this.skeleton = skeleton;
            this.socket = socket;
        }catch(Exception e){
            System.err.println("Construct Error");
            e.printStackTrace();
        }
    }

    public ClientHandler(InetAddress address, int port){
        try {
            socket = new Socket(address, port);
            System.out.println("Connected");
        }
        catch(IOException e){
            System.err.println("IO Error");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();
    }
}
