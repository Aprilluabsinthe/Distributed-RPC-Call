package PingPongTest;

import rmi.RMIException;

/**
 * PingPongServer
 * the Java class that represents the actual server machine
 */

public class PingPongServer implements PingServer{

    @Override
    public String ping(int idNumber) throws RMIException {
        String result = "Pong" + Integer.toString(idNumber);
        return result;
    }
}
