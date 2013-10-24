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
	public byte[] createHeader(int payloadLen,int psecret,int step, int student) {
		//not working yet!!!!
    	byte[] header = new byte[12];
    	byte[] payloadLen_b = new byte[4];
    	byte[] psecret_b = new byte[4];
    	byte[] step_b = new byte[2];
    	byte[] student_b = new byte [2];
    	
    	//convert to byte
    	payloadLen_b=ByteBuffer.allocate(4).putInt(payloadLen+12).array(); // +12 for the size of header?
    	psecret_b=ByteBuffer.allocate(4).putInt(psecret).array();
    	step_b=ByteBuffer.allocate(4).putInt(step).array();
    	student_b=ByteBuffer.allocate(4).putInt(student).array();
    	
    	//copy to header
    	System.arraycopy(payloadLen_b,0,header,0,4);
    	System.arraycopy(psecret_b,0,header,4,4);
    	System.arraycopy(step_b,2,header,8,2);
    	System.arraycopy(student_b, 2, header, 10, 2);
    	
    	return header;
	}
}
