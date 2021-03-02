package PingPong;

import rmi.RMIException;

import java.net.UnknownHostException;

/**
 * Created by saurabh on 30/04/16.
 */
public interface PingPongFactoryInterface {
    public PingPongInterface makePingServer() throws RMIException, UnknownHostException;
}
