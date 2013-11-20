package Project2;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Runs both the client and Server to test the server
 * @author benjamin
 *
 */
public class ServerRunner {
	public static final int MAX_CONNECTIONS = 50;
		
	public static void main(String[] args) {
		byte[] buffer = new byte[ServerValuesHolder.HEADER_LENGTH + ServerValuesHolder.payloadInit.length]; // header length + payload length = 12 + 8
		
		try{
			int count = 0;
			
			DatagramSocket socket = new DatagramSocket(12235);	
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			// receive request
			
			while((count++ < MAX_CONNECTIONS) || (MAX_CONNECTIONS == 0)){
				
				socket.receive(packet);
				byte[] receivedData = packet.getData();
				if(receivedData.length > ServerValuesHolder.HEADER_LENGTH){
					ServerMain server= new ServerMain(packet);
					Thread t = new Thread(server);
					t.start();
					socket.close();
				}
			}
		}catch (Exception e){
			System.out.println("IOException caught: " + e.getMessage());
			e.printStackTrace();
			return ;
		
		}
		
	}

}
