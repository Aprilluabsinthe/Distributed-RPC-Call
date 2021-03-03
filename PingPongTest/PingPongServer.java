package pingpongtest;

import rmi.RMIException;
import rmi.Skeleton;
import java.net.*;
import java.io.Serializable;

/**
 * PingPongServer
 * the Java class that represents the actual server machine
 * @author Di Lu, Yuan Gu
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
