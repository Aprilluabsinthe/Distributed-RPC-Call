package pingpongtest;
import rmi.RMIException;

/**
 * The class generates a PingServer
 * s a Java interface that is used on both sides of the RMI connection
 * it contains a prototype for a single function called
 * @@author Di Lu
 * @throws RMIException Throws RMIException if
 */
public interface PingServer {
    /**
     * <p>
     * The client should invoke a String ping(int idNumber) method upon the server,
     * server would return a String containing Pong idNumber
     *
     * <p>
     * idNumber is the string representation of the integer sent by the client
     *
     * @param idNumber
     * @return String "Pong + idNumber"
     * @throws RMIException
     */
    public String ping( int idNumber) throws RMIException;
}