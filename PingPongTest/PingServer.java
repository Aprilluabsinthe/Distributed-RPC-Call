package PingPongTest;
import rmi.RMIException;

/**
 * The class generates a PingServer
 * @@author Di Lu
 */
public class PingServer {
    public String ping( int idNumber) throws RMIException {
        String pongstr = "Pong" + Integer.toString(idNumber);
        return pongstr;
    }
}