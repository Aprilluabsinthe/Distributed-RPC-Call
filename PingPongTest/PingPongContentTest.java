package PingPongTest;

import rmi.RMIException;

public class PingPongContentTest {
    public void content(int number){}
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
