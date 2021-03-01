package PingPongTest;

import rmi.RMIException;

import java.io.Serializable;

/**
 * PingPongServer
 * the Java class that represents the actual server machine
 */

public class PingPongServer implements PingServer, Serializable {
    private static final long serialVersionUID = -2808996835410686176L;

    /**
     * <p>
     *     The client invoke a <code>String ping(int idNumber)</code> method upon the server
     * </p>
     *
     * @param idNumber
     * @return
     * @throws RMIException
     */
    @Override
    public String ping(int idNumber) throws RMIException {
        String result = "Pong" + Integer.toString(idNumber);
        return result;
    }
}
