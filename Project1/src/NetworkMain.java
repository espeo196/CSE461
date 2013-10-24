import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Main class for handling UDP/TCP sending and receiving. 
 * @author espeo
 *
 */


public class NetworkMain {
	public static void main(String args[]) {
		NetworkSend send = new NetworkSend();
		NetworkReceive receive = new NetworkReceive();
		
		send.sendStageA();
		receive.receiveStageA(send.getSocket());
	}
}
