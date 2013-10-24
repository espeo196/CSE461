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
	
	private DatagramSocket socket;
	private InetAddress serverAddress;
	
	/**
	 * Constructor that instantiates a DatagramSocket and InetAddress
	 * for the current session.
	 */
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

		// TODO: call createHeader and combine byte arrays
		byte[] header = createHeader(b.length, 1, "0");
		
		// TODO: should be passing a byte[] that includes the payload and the header
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
	 *
	 * @param length : int value of payload length in bytes
	 * @param step : integer representation of current step
	 * @param psecret : secret from the previous step
	 */
	public byte[] createHeader(int length, int step, String psecret) {
		// TODO: put the above information into a byte array that is aligned correctly
		
		// Note: the payload_len field should be: length + the header's length
		int payload_len = length + 12;
		
		return null;
	}
	
	public DatagramSocket getSocket() {
		return socket;
	}
	
	public InetAddress getAddress() {
		return serverAddress;
	}
}
