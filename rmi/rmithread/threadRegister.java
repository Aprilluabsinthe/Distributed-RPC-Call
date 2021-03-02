package rmi.rmithread;

import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The Register for threads
 * in essencial is a HashMap, storing the sockt ad skeletonThread
 * Every Skeleton will maintain a threadRegister
 */
public class threadRegister<T> {
    private volatile Map<Socket, SkeletonThread<T>> map = new HashMap<>();

    /**
     * add Thread into threadRegister
     * @param socket
     * @param skeletonThread
     */
    public void addClientTask(Socket socket,SkeletonThread skeletonThread) {
        this.map.put(socket,skeletonThread);
    }

    /**
     * remove Thread from threadRegister
     * @param socket
     */
    public void removeClientTask(Socket socket) {
        this.map.remove(socket);
    }

    /**
     * the iterator() for threadRegister
     * @return a Iterator for Iteration
     */
    public Iterator iterator() {
        return map.values().iterator();
    }

    /**
     * the next() for threadRegister
     * @return the next Thread in Map
     */
    public SkeletonThread next() {
        if(iterator().hasNext()){
            return map.values().iterator().next();
        }
        else return null;
    }

    /**
     * The isEmpty() for threadRegister
     * @return whenther the threadRegister is Emoty or Not
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * The claer() for threadRegister
     * Clear all registers
     */
    public void clear() {
        map.clear();
    }
}
