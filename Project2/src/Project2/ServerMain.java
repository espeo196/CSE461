package Project2;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

/**
 * 
 * @author Nicholas Johnson, Benjamin Chan
 *
 */
public class ServerMain {

	public static final int HEADER_LENGTH = 12;
	public static int studentID;
	public static int psecretInit;
	public static int psecretA;
	public static int psecretB;
	public static int psecretC;
	public static int psecretD;
	
	/**
	 * Perform stage A
	 * receive a packet from client
	 * transmit a packet if it the client's packet is valid 
	 */
	public static void stageA() {
		// establish server socket
		int studentID=-1;
		psecretInit=0;
		psecretA=generateSecret();
		try {
			DatagramSocket socket = new DatagramSocket(12235);
			
			byte[] buffer = new byte[HEADER_LENGTH + 4]; // header length + payload length = 12 + 4
			
			// receive request
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			socket.receive(packet);
			//receive data
			byte[] receivedData = packet.getData();
			
			//get student id
			if(receivedData.length>12){
				studentID=byteArrayToInt(Arrays.copyOfRange(receivedData, 10, 12),0);
			}
			// verify header
			if(studentID!=-1 && PacketVerifier.stageA(receivedData,studentID,psecretInit)) {
				
				byte[] data=PacketCreater.stageA(studentID,psecretA);
				DatagramPacket sendPacket = new DatagramPacket(data, data.length, packet.getAddress(), packet.getPort());
				socket.send(sendPacket);
			}
			socket.close();
		} catch (SocketException e) {
			System.out.println("SocketException caught: " + e.getMessage());
			e.printStackTrace();
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
	private static int generateSecret() {
		Random random=new Random();
		int randomNum=random.nextInt(2147483647); //largest number in int
		return randomNum;
	}
}
