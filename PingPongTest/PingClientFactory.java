package PingPongTest;

import rmi.RMIException;
import rmi.Stub;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * Similar to PingServerFactory, the Factory for Client PingServer
 * @author Di Lu
 * <p>
 *     Factory for client to generate a PingServer, which needs to initiate a Proxy
 *     given the address(hostname and port)
 *     call <code>Stub.create()</code> and <code>makePingServer()</code> in sequence
 *
 * <p>
 *     There is no need to pass extra interfaces to the Stub,
 *     and the method need to be static
 *     thus do not implement extra interfaces
 */
public class PingClientFactory{

    /**
     * Initiate Proxy for PingPongClient
     * <p>
     *     The PingPongClient needs a way to instantiate this proxy
     *     created using Stub.create(...), and PingFactory.makePingServer()
     *     return the local proxy that the client can interact with as a remote object reference
     * <p>
     *     ues hostname and port to create a unresolved new <code>InetSocketAddress</code>
     *     call <code>Stub.create()</code> to create a stub proxy handler, which is a server(stub factory)
     *     call the <code>makePingServer()</code> method in the <code>StubFactory</code>
     *
     *
     * @param hostname
     * @param port
     * @return PingServer pingStub
     */

    public static PingServer initProxy(String hostname, int port){
        InetSocketAddress address = InetSocketAddress.createUnresolved(hostname, port);
        ServerFactoryInterface StubFactory = null;
        try{
            StubFactory = Stub.create(ServerFactoryInterface.class,address);
        }catch(Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }catch(Error err){
            System.err.println(err.getMessage());
        }

        PingServer pingStub = null;
        try {
            pingStub = StubFactory.makePingServer();
        }catch(RMIException | UnknownHostException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return pingStub;
    }
}
