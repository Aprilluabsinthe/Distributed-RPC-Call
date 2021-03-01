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

public class PingServerFactory implements FactoryInterface {
    @Override
    public PingServer makePingServer() throws RMIException, UnknownHostException {
        PingPongServer ppserver = new PingPongServer();
        Skeleton<PingServer> skeleton = new Skeleton<>(PingServer.class, ppserver);
        skeleton.start();
        PingServer ppstub = Stub.create(PingServer.class,skeleton);
        return ppstub;
    }

    public static void main(String[] args) throws RMIException, UnknownHostException {
        if(args.length < 1){
            System.err.println("Expecting Port");
        }
        InetSocketAddress address = new InetSocketAddress(Integer.parseInt(args[0]));
        PingServerFactory factory = new PingServerFactory();
        Skeleton<FactoryInterface> skeleton = new Skeleton<FactoryInterface>(FactoryInterface.class,factory,address);
        try{
            skeleton.start();
        }catch(RMIException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

}
