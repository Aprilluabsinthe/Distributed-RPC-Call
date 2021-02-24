package rmi.rmiThread;

import rmi.Skeleton;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class ClientTask<T> extends Thread{
    private Socket socket = null;
    private Skeleton<T> skeleton;
    private SocketAddress address;
    private boolean isRunning = False;

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
        try{
            ObjectInputStream instream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream outstream = new ObjectOutputStream(socket.getOutputStream());
            Object object = null;
            while(isRunning){
                // TODO: how to read objects?
            }
        } catch (IOException e) {
            System.err.println("Client Task IOError");
            e.printStackTrace();
        }

    }
}
