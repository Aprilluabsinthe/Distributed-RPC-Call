package PingPongTest;
import rmi.RMIException;

/**
 * The class generates a PingServer
 * s a Java interface that is used on both sides of the RMI connection
 * it contains a prototype for a single function called
 * @@author Di Lu
 */
public interface PingServer {
    public String ping( int idNumber) throws RMIException;
}