package Project2;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;

/**
 * 
 * @author Nicholas Johnson, Benjamin Chan
 *
 */
public class ServerMain {

	
	public ServerValuesHolder values;

	
	public ServerMain() {
		values = new ServerValuesHolder();
	}
	
	/**
	 * Perform stage A
	 * receive a packet from client
	 * transmit a packet if it the client's packet is valid 
	 */
	public void stageA() {
		// establish server socket
		
		
		try {
			DatagramSocket socket = new DatagramSocket(12235);
			
			byte[] buffer = new byte[ServerValuesHolder.HEADER_LENGTH+values.getPayloadInit().length]; // header length + payload length = 12 + 8
			
			// receive request
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			socket.receive(packet);
			byte[] receivedData = packet.getData();
			
			// verify header and that the received packet is long enough
			if(receivedData.length > 12 && PacketVerifier.verifyStageA(receivedData, values)) {
				values.setStudentID(Arrays.copyOfRange(receivedData, 10, 12));		
				
				byte[] data = PacketCreater.stageAPacket(values);
				DatagramPacket sendPacket = new DatagramPacket(data, data.length, packet.getAddress(), packet.getPort());
				socket.send(sendPacket);
			}else{
				System.out.println("stage A packet not valid");
			}
			socket.close();
		} catch (IOException e) {
			System.out.println("IOException caught: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Perform stage B
	 * receive and acknowledge several packets.
	 */
	public void stageB() {
		try {
			Random rand = new Random();
			InetAddress senderAddress = null;
			int senderPort = 0;
			
			DatagramSocket socket = new DatagramSocket(values.getUdp_portInit());
			
			byte[] buffer = new byte[ServerValuesHolder.HEADER_LENGTH + values.getLen() + 4];
			
			for(int i = 0; i < values.getNum(); i++) {
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				// randomly determine whether to ACK or not
				if(rand.nextBoolean()) {					
					byte[] receivedData = packet.getData();
					senderAddress = packet.getAddress();
					senderPort = packet.getPort();

					// extract packet_id
					if(receivedData.length > 12 && PacketVerifier.verifyStageB(receivedData, values, i)) {
					
						byte[] data = PacketCreater.stageBAck(values, i);
						DatagramPacket sendPacket = new DatagramPacket(data, data.length, senderAddress, senderPort);
						socket.send(sendPacket);
					} else {
						// malformed packet received
						socket.close();
						break;
					}
				} else {
					i--; // chose not to acknowledge. decrement counter
				}
			}
			
			if(senderAddress != null) {
				byte[] data = PacketCreater.stageBPacket(values);
				DatagramPacket sendPacket = new DatagramPacket(data, data.length, senderAddress, senderPort);
				socket.send(sendPacket);
			}		
			socket.close();
		} catch (IOException e) {
			System.out.println("IOException caught: " + e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Perform stageC. Set up a tcp connection and send a response.
	 */
	public void stageC() {
		try {
			ServerSocket socket = new ServerSocket(values.getTcp_port());
			Socket connectionSocket = socket.accept();
			
			System.out.println("Server has connected.");
			byte[] data = PacketCreater.stageCPacket(values);
			
			DataOutputStream out = new DataOutputStream(connectionSocket.getOutputStream());
			out.write(data);
			
		} catch (IOException e) {
			System.out.println("IOException caught: " + e.getMessage());
			e.printStackTrace();
		}
	}
	

}
