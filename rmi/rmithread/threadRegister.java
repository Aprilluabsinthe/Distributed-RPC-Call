package rmi.rmithread;

import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class threadRegister<T> {
    private volatile Map<Socket, SkeletonThread<T>> map = new HashMap<>();

    public void addClientTask(Socket socket,SkeletonThread skeletonThread) {
        this.map.put(socket,skeletonThread);
    }

    public void removeClientTask(Socket socket) {
        this.map.remove(socket);
    }

    public Iterator iterator() {
        return map.values().iterator();
    }

    public SkeletonThread next() {
        if(iterator().hasNext()){
            return map.values().iterator().next();
        }
        else return null;
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public void clear() {
        map.clear();
    }
}
