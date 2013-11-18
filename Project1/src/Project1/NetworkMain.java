package Project1;
import java.io.*;
import java.net.*;

/**
 * Main class for handling UDP/TCP sending and receiving. 
 * 
 * @author Nicholas Johnson, Benjamin Chan
 *
 */
public class NetworkMain {

	/**
	 * String name of the server to connect to.
	 */
	//public static final String SERVER_NAME = "bicycle.cs.washington.edu";
	public static final String SERVER_NAME = "localhost";
	
	/**
	 * Port value for initially connecting and sending/receiving Datagrams.
	 */
	public static final int PORT = 12235;
	
	public static void main(String args[]) throws IOException {
		DatagramSocket socket = null;
		InetAddress serverAddress = null;
		
		// Set up the UDP socket and get the InetAddress for the SERVER_NAME.
		try {
			socket = new DatagramSocket();
			serverAddress = InetAddress.getByName(SERVER_NAME);
			
		} catch (IOException e) {
			System.out.println("IOException caught: " + e.getMessage());
			e.printStackTrace();
		}
		
		// Use helper methods to perform the bulk of the work
		byte[] dataB = performStageAB(socket, serverAddress);
		
		if(dataB != null) {
			performStageCD(dataB);			
		}
	}

	/**
	 * Complete stages A and B. This includes sending and receiving UDP packets and printing their contents.
	 * 
	 * @param socket DatagramSocket used to send/receive data over. 
	 * @param serverAddress InetAddress of the server to connect to.
	 * @return a byte[] containing information from stage B. Returns null if and error occurred.
	 */
	public static byte[] performStageAB(DatagramSocket socket, InetAddress serverAddress) {
		byte[] dataA;
		byte[] dataB = null;
		
		try {
			NetworkSend.sendStageA(socket, serverAddress, PORT);
			dataA = NetworkReceive.listen(socket, 1000);
			printPacket(dataA,"----------------stage a result----------------");
			NetworkSend.sendStageB(socket, serverAddress, byteArrayToInt(dataA, 12), 
					byteArrayToInt(dataA, 16), byteArrayToInt(dataA, 20), byteArrayToInt(dataA, 24));
			dataB = NetworkReceive.listen(socket, 1000);
			printPacket(dataB,"----------------stage b result----------------");
			
		} catch (IOException e) {
			System.out.println("IOException caught: " + e.getMessage());
			e.printStackTrace();
		}
		
		return dataB;
	}
	
	/**
	 * Complete stages C and D. This includes sending and receiving with TCP and printing the results.
	 * 
	 * @param data byte[] containing data from stage B with num, len, and secretB fields in that order.
	 */
	public static void performStageCD(byte[] data) {
		Socket tcpSocket;
		try {
			tcpSocket = new Socket(SERVER_NAME, byteArrayToInt(data, 12));
			byte[] dataC = NetworkReceive.listenTCP(tcpSocket, 1000); 
			NetworkMain.printPacket(dataC, "----------------stage c result----------------");
			NetworkSend.sendStageD(tcpSocket, byteArrayToInt(dataC, 12), byteArrayToInt(dataC, 16), 
					byteArrayToInt(dataC, 20), dataC[24]);
			byte[] dataD = NetworkReceive.listenTCP(tcpSocket, 1000);
			NetworkMain.printPacket(dataD, "----------------stage d result----------------"); 
			
			tcpSocket.close();
		} catch (IOException e) {
			System.out.println("IOException caught: " + e.getMessage());
			e.printStackTrace();
		}
	}
	

	
	/**
	 * Print out the content of the packet
	 *
	 * @param packet byte[] to have its contents printed.
	 * @param title String to be shown before printing packet contents.
	 */
	public static void printPacket(byte[] packet, String title){
		if(title!=null)
			System.out.println(title);
		if(packet!=null){
			System.out.println("Packet Header:");
			
			for (int j=0; j < 12; j++) {
				System.out.format("0x%x ", packet[j]);
		 		if((j+1)%4 == 0) {
		        	System.out.println();
		        }
	 		}
			
			System.out.println("Packet Content:");
			for (int j=12; j < 28; j++) {
				System.out.format("0x%x ", packet[j]);
		 		if((j+1)%4 == 0) {
		 			System.out.println();
		        }
			}
			System.out.println("---------------");
		}else{
			System.out.println("null");
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
