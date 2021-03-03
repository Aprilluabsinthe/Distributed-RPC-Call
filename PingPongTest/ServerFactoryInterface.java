package pingpongtest;

import rmi.RMIException;

import java.net.UnknownHostException;

/**
 * PingPong Server Factory Interface
 *
 * <p>
 *     The Interface is used for getting interfaces <code> Class<T> c </code>
 *     To get Class Interfaces, call FactoryInterfaces.class
 * @author Di Lu, Yuan Gu
 * @throws RMIException
 * @throws UnknownHostException
 */

public interface ServerFactoryInterface {
    /**
     * <p>
     * PingServerFactory which contains a PingServer makePingServer() method
     * makePingServer() method create a new PingServer
     * return the PingServer as a remote object reference.
     *
     * @return PingServer a remote object reference.
     * @throws RMIException
     * @throws UnknownHostException
     */
    public PingServer makePingServer() throws RMIException, UnknownHostException;
}
