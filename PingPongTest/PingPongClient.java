package PingPongTest;

import rmi.RMIException;
import rmi.Stub;
import PingPongTest.PingPongContentTest;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class PingPongClient {
    public static void main(String[] args) throws RMIException, UnknownHostException {
        if(args.length < 2){
            System.err.println("Expecting remote hostname and address");
        }
        InetSocketAddress address = InetSocketAddress.createUnresolved(args[0], Integer.parseInt(args[1]));
        FactoryInterface StubFactory = Stub.create(FactoryInterface.class,address);
        PingServer pingStub = null;
        try {
            pingStub = StubFactory.makePingServer();
        }catch(RMIException | UnknownHostException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
//        PingPongContentTest.testContenent(pingStub,i);
    }



}
