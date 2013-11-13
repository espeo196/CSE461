package Project2;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Random;

/**
 * 
 * @author Nicholas Johnson, Benjamin Chan
 *
 */
public class ServerMain {

	public static final int HEADER_LENGTH = 12;
	public static ValuesHolder values;
	public static int studentID;
	public static int psecretInit;
	
	public ServerMain() {
		Random rand = new Random();
		values = new ValuesHolder();
		
		// TODO: should all be randomly generated and put in their correct places
		
		values.lenA = rand.nextInt(20);
		values.numA = rand.nextInt(20);
		values.lenC = rand.nextInt(20);
		values.numC = rand.nextInt(20);
		
		values.secretA = generateSecret();
		values.secretB = generateSecret();
		values.secretC = generateSecret();
		values.secretD = generateSecret();
		
		values.tcp_portB = 2222;
		values.udp_portA = 2222;
	}
	
	/**
	 * Perform stage A
	 * receive a packet from client
	 * transmit a packet if it the client's packet is valid 
	 */
	public void stageA() {
		// establish server socket
		studentID = -1;
		psecretInit = 0;
		
		try {
			DatagramSocket socket = new DatagramSocket(12235);
			
			byte[] buffer = new byte[HEADER_LENGTH + 4]; // header length + payload length = 12 + 4
			
			// receive request
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			socket.receive(packet);
			//receive data
			byte[] receivedData = packet.getData();
			
			//get student id
			if(receivedData.length > 12) {
				studentID = byteArrayToInt(Arrays.copyOfRange(receivedData, 10, 12), 0);
			}
			// verify header
			if(studentID != -1 && PacketVerifier.verifyStageA(receivedData, studentID, psecretInit)) {
				
				byte[] data = PacketCreater.stageAPacket(studentID, values.secretA);
				DatagramPacket sendPacket = new DatagramPacket(data, data.length, packet.getAddress(), packet.getPort());
				socket.send(sendPacket);
			}
			socket.close();
		} catch (IOException e) {
			System.out.println("IOException caught: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void stageB() {
		try {
			Random rand = new Random();
			InetAddress senderAddress = null;
			int senderPort = 0;
			
			DatagramSocket socket = new DatagramSocket(values.udp_portA);
			
			byte[] buffer = new byte[HEADER_LENGTH + values.lenA + 4];
			
			for(int i = 0; i < values.numA; i++) {
				// randomly determine whether to ACK or not
				while(rand.nextBoolean()) {
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
					socket.receive(packet);
					
					byte[] receivedData = packet.getData();
					senderAddress = packet.getAddress();
					senderPort = packet.getPort();
					
					
					if(receivedData.length > 12) {
						studentID = byteArrayToInt(Arrays.copyOfRange(receivedData, 10, 12), 0);
					}
					// extract packet_id
					if(PacketVerifier.verifyStageB(receivedData, studentID, values.secretA, i)) {
						
							byte[] data = PacketCreater.stageBAck(studentID, i);
							DatagramPacket sendPacket = new DatagramPacket(data, data.length, senderAddress, senderPort);
							socket.send(sendPacket);
					}
				}				
			}
			
			if(senderAddress != null) {
				byte[] data = PacketCreater.stageBPacket(studentID, values.secretB);
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
	 * Convert the byte array to an int.
	 *
	 * @param b The byte array
	 * @param offset The array offset
	 * @return The integer
	 */
	public static int byteArrayToInt(byte[] b, int offset) {
	    int value = 0;
	    for (int i = 0; i < 4; i++) {
	        int shift = (4 - 1 - i) * 8;
	        value += (b[i + offset] & 0x000000FF) << shift;
	    }
	    return value;
	}
	
	/**
	 * Randomly generates a 4 byte secret and returns it
	 * @return a 4 byte integer containing a randomly generated secret
	 */
	private int generateSecret() {
		Random random = new Random();
		int randomNum=random.nextInt(2147483647); //largest number in int
		return randomNum;
	}
}
