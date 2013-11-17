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

	public static final int HEADER_LENGTH = 12;
	public static ValuesHolder values;
	public static byte[] studentID;
	public static int psecretInit;
	
	public ServerMain() {
		studentID = new byte[2];
		
		Random rand = new Random();
		values = new ValuesHolder();
		values.c = 'c'; // TODO: needs to be random		
		
		values.lenA = rand.nextInt(20);
		values.numA = rand.nextInt(20);
		values.lenC = rand.nextInt(20);
		values.numC = rand.nextInt(20);
		
		values.secretA = generateSecret();
		values.secretB = generateSecret();
		values.secretC = generateSecret();
		values.secretD = generateSecret();
		
		// TODO: needs to be random and in correct valid range
		values.tcp_portB = 12235;
		values.udp_portA = 12235;
	}
	
	/**
	 * Perform stage A
	 * receive a packet from client
	 * transmit a packet if it the client's packet is valid 
	 */
	public void stageA() {
		// establish server socket
		psecretInit = 0;
		
		try {
			DatagramSocket socket = new DatagramSocket(12235);
			
			byte[] buffer = new byte[HEADER_LENGTH + 4]; // header length + payload length = 12 + 4
			
			// receive request
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			socket.receive(packet);
			byte[] receivedData = packet.getData();
			
			// verify header and that the received packet is long enough
			if(receivedData.length > 12 && PacketVerifier.verifyStageA(receivedData, psecretInit)) {
				studentID = Arrays.copyOfRange(receivedData, 10, 12);		
				
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
	
	/**
	 * Perform stage B
	 * receive and acknowledge several packets.
	 */
	public void stageB() {
		try {
			Random rand = new Random();
			InetAddress senderAddress = null;
			int senderPort = 0;
			
			DatagramSocket socket = new DatagramSocket(values.udp_portA);
			
			byte[] buffer = new byte[HEADER_LENGTH + values.lenA + 4];
			
			for(int i = 0; i < values.numA; i++) {
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				// randomly determine whether to ACK or not
				if(rand.nextBoolean()) {					
					byte[] receivedData = packet.getData();
					senderAddress = packet.getAddress();
					senderPort = packet.getPort();

					// extract packet_id
					if(receivedData.length > 12 && PacketVerifier.verifyStageB(receivedData, values.secretA, i)) {
						studentID = Arrays.copyOfRange(receivedData, 10, 12);		
					
						byte[] data = PacketCreater.stageBAck(studentID, i);
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
	 * Perform stageC. Set up a tcp connection and send a response.
	 */
	public void stageC() {
		try {
			ServerSocket socket = new ServerSocket(values.tcp_portB);
			Socket connectionSocket = socket.accept();
			
			System.out.println("Server has connected.");
			byte[] data = PacketCreater.stageCPacket(studentID, values.secretC);
			
			DataOutputStream out = new DataOutputStream(connectionSocket.getOutputStream());
			out.write(data);
			
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
