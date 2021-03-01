package PingPongTest;

import rmi.RMIException;


/**
 * the  a PingServerFactory
 * which contains a PingServer makePingServer() method
 * that should create a new PingServer and
 * return it as a remote object reference
 * @return a <code>PingServer</code> instance
 */

public class PingServerFactory {
    public PingServer makePingServer() throws RMIException {
        return new PingServer();
    }
}
