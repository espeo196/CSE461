import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Class for sending network packets.
 * @author espeo
 *
 */

public class NetworkSend {
	public final String SERVER_NAME = "bicycle.cs.washington.edu";
	public final int PORT = 12235;
	
	public DatagramSocket socket;
	public InetAddress serverAddress;
	
	public NetworkSend() {
		try {
			socket = new DatagramSocket();
			serverAddress = InetAddress.getByName(SERVER_NAME);
		} catch (SocketException | UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends a UDP packet containing the string "hello world."
	 */
	public void sendStageA() {
		String payload = "hello world";
		byte[] b = payload.getBytes();
		
		// TODO: call createHeader to combine byte arrays
		
		DatagramPacket packet = new DatagramPacket(b, b.length, serverAddress, PORT);
		
		try {
			socket.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates the packet header with the following format:
	 *  0               1               2               3  
 	 *	0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7
	 *	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *	|                           payload_len                         |
	 *	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *	|                             psecret                           |
	 *	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *	|             step               |  last 3 digits of student #  |
	 *	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 */
	public byte[] createHeader(int length, String step, String psecret) {
		// TODO: put the above information into a byte array that is aligned correctly
		return null;
	}
}
