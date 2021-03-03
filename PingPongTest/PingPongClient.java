package pingpongtest;

import rmi.*;

import java.net.UnknownHostException;

/**
 * PingPong Client
 * @author Di Lu, Yuan Gu
 * <p>
 *     the Java class that represents the actual server machine
 */
public class PingPongClient {
    /** the main fucntion for PingPongClient
     * <p>
     *     validate the arguments, should given hostname and address
     *     crate a ew PingServer with the hostname and the port
     *     testContent by printing the messages returned from the server to the console.
     *
     * @param args hostname and address
     * @throws RMIException
     * @throws UnknownHostException
     */
    public static void main(String[] args) throws RMIException, UnknownHostException {
        if(args.length < 3){
            System.err.println("Expecting hostname, address and Numbers sending");
        }
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        int number = Integer.parseInt(args[2]);
        PingServer pingStub = PingClientFactory.initProxy(hostname,port);
        System.out.println("Print PingPong Result");
        testContent(pingStub,2);
    }

    /**
     * The testing method for client side proxy stub
     * <p>
     *     print the response to console for validating
     * </p>
     *
     * @param pingStub
     * @param number the number of ids to be tested and printed
     * @throws RMIException
     */
    public static void testContent(PingServer pingStub, int number) throws RMIException {
        for (int i = 0 ; i <= number; i++){
            String response = pingStub.ping(i);
            if (response == null){
                throw new RMIException("PingPongClient received no response");
            }
            System.out.printf("response for %d is : %s\n",i,response);
            String origin = "Pong" + i;
            if( response == origin ){
                System.out.printf("response for %d is sueccessful");
            }
            else{
                System.out.printf("response for %d failed");
            }
        }
    }
}
