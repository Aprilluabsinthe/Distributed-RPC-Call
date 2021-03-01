package PingPongTest;

import rmi.RMIException;

import java.net.UnknownHostException;

public interface FactoryInterface {
    public PingServer makePingServer() throws RMIException, UnknownHostException;
}
