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
	public static BufferedReader reader;
	public static Socket tcpSocket;
	public static void main(String args[]) throws IOException {
		setupAB();
		NetworkSend.sendStageA(socket,serverAddress,PORT);
		byte[] dataA = NetworkReceive.listen(socket, 1000);
		printPacket(dataA,"----------------stage a result----------------");
		NetworkSend.sendStageB(socket, serverAddress, byteArrayToInt(dataA, 12), byteArrayToInt(dataA, 16), byteArrayToInt(dataA, 20), byteArrayToInt(dataA, 24));
		byte[] dataB = NetworkReceive.listen(socket, 1000);
		printPacket(dataB,"----------------stage b result----------------");
		NetworkSend.sendStageC(tcpSocket,serverAddress,byteArrayToInt(dataB, 12),byteArrayToInt(dataB, 16));
		byte[] dataC=NetworkReceive.listenTCP(SERVER_NAME, 1000,byteArrayToInt(dataB, 12)); 
		NetworkMain.printPacket(dataC, "----------------stage c result----------------");
	}
	
	/**
	 * Setup the initial DatagramSocket and serverAddress.
	 */
	public static void setupAB(){
		try {
			//for statge a b
			socket = new DatagramSocket();
			serverAddress = InetAddress.getByName(SERVER_NAME);
		} catch (IOException e) {
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
	/**
	 * Print out the content of the packet
	 *
	 * @param packet content of the packet
	 * @param title title shown
	 * @return void
	 */
	public static void printPacket(byte[]packet,String title){
		System.out.println(title);
		System.out.println("Packet Header:");
		for (int j=0;j<12;j++) {
	 		   System.out.format("0x%x ", packet[j]);
	 		  if((j+1)%4 == 0) {
	        		System.out.println();
	        	}
 		}
		System.out.println("Packet Content:");
		for (int j=12;j<28;j++) {
	 		   System.out.format("0x%x ", packet[j]);
	 		  if((j+1)%4 == 0) {
	        		System.out.println();
	        	}
		}
		System.out.println("---------------");
	}
}
