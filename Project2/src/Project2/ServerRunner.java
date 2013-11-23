package Project2;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Runs both the client and Server to test the server
 * @author Benjamin Chan, Nicholas Johnson
 *
 */
public class ServerRunner {
	public static final int MAX_CONNECTIONS = 50;
		
	public static void main(String[] args) {
		// array length = header length + payload length = 12 + 8
		byte[] buffer = new byte[ServerValuesHolder.HEADER_LENGTH + ServerValuesHolder.payloadInit.length]; 
		
		try {
			DatagramSocket socket = new DatagramSocket(12235);	
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			System.out.println("CSE461 Project2 Server Started");
			// receive request
			while(true) {	
				socket.receive(packet);
				byte[] receivedData = packet.getData();
				ServerMain.printPacket(receivedData, "Received stage A packet"
						+" from: "+packet.getAddress().getHostAddress()
						+" : "+packet.getPort());
				if(receivedData.length > ServerValuesHolder.HEADER_LENGTH){
					ServerMain server= new ServerMain(packet,socket);
					Thread t = new Thread(server);
					t.start();
				}
			}
		} catch (Exception e) {
			System.out.println("IOException caught: " + e.getMessage());
			e.printStackTrace();
			return ;
		}
	}
}
