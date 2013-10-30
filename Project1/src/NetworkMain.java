/**
 * Main class for handling UDP/TCP sending and receiving. 
 * @author espeo
 *
 */
import java.io.*;
import java.net.*;

public class NetworkMain {

	public static final String SERVER_NAME = "bicycle.cs.washington.edu";
	public static final int PORT = 12235;
	public static DatagramSocket socket;
	public static InetAddress serverAddress;
	public static void main(String args[]) throws IOException {
		setup();
		NetworkSend.sendStageA(socket,serverAddress,PORT);
		byte[] dataA = NetworkReceive.listen(socket, 1000);
		NetworkSend.sendStageB(socket, serverAddress, 2358, byteArrayToInt(dataA, 0), byteArrayToInt(dataA, 4), byteArrayToInt(dataA, 12));
		byte[] dataB = NetworkReceive.listen(socket, 1000);
		
	}
	
	/**
	 * Setup the initial DatagramSocket and serverAddress.
	 */
	public static void setup(){
		try {
			socket = new DatagramSocket();
			serverAddress = InetAddress.getByName(SERVER_NAME);
		} catch (SocketException | UnknownHostException e) {
			System.out.println("Exception caught: " + e.getMessage());
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
}
