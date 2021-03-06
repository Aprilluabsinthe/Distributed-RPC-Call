package PingPongTest;

import rmi.RMIException;
import rmi.Skeleton;
import rmi.Stub;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;


/**
 * the  a PingServerFactory
 * which contains a PingServer makePingServer() method
 * that should create a new PingServer and
 * return it as a remote object reference
 * @return a <code>PingServer</code> instance
 */

public class PingServerFactory implements ServerFactoryInterface {
    /**
     *
     * @return
     * @throws RMIException
     * @throws UnknownHostException
     */
    @Override
    public PingServer makePingServer() throws RMIException, UnknownHostException {
        PingPongServer serverPingServer = new PingPongServer();
        Skeleton<PingServer> skeleton = new Skeleton<>(PingServer.class, serverPingServer);
        skeleton.start();
        PingServer ppstub = Stub.create(PingServer.class,skeleton);
        return ppstub;
    }

    /**
     * main function
     * <p>
     *     validate the args
     *     start a skeleton by Port
     * </p>
     * @param args Port
     * @throws RMIException
     * @throws UnknownHostException
     */
    public static void main(String[] args) throws RMIException, UnknownHostException {
        if(args.length < 1){
            System.err.println("Expecting Port");
        }
        InetSocketAddress address = new InetSocketAddress(Integer.parseInt(args[0]));
        PingServerFactory factory = new PingServerFactory();
        Skeleton<ServerFactoryInterface> skeleton = new Skeleton<>(ServerFactoryInterface.class, factory, address);
        try{
            skeleton.start();
        }catch(RMIException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

}
