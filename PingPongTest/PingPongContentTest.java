package pingpongtest;

import rmi.RMIException;

/**
 * the Helper class for PingPong context test
 * @author Di Lu
 */
public class PingPongContentTest {
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
