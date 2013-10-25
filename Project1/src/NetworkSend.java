import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

/**
 * Class for sending network packets.
 * @author espeo
 *
 */

public class NetworkSend {
	
	
	public DatagramSocket socket;
	
	
//	public NetworkSend() {
//		
//	}
	
	/**
	 * Sends a UDP packet containing the string "hello world."
	 */
	public static void sendStageA(DatagramSocket socket,InetAddress serverAddress, int port) {
		//put the string into an array and add the null terminator
		String payload = "hello world";
    	byte[] b =new byte[payload.getBytes().length+1];
    	System.arraycopy(payload.getBytes(),0,b,0,payload.getBytes().length);
    	b[b.length-1]=0;
    	
    	//generate the header
    	byte[] header=createHeader(b.length,0,1,856);
    	
    	//combine the header and payload and make sure it is aligned on a 4-byte boundary
    	byte[] message=new byte[(int) (4*(Math.ceil((header.length+b.length)/4.0)))];
    	System.arraycopy(header, 0, message, 0, header.length);
    	System.arraycopy(b,0,message,header.length,b.length);
    	
		DatagramPacket packet = new DatagramPacket(message, message.length, serverAddress, port);
		
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
	private static byte[] createHeader(int payloadLen,int psecret,int step, int student) {
    	byte[] header = new byte[12];
    	byte[] payloadLen_b = new byte[4];
    	byte[] psecret_b = new byte[4];
    	byte[] step_b = new byte[2];
    	byte[] student_b = new byte [2];
    	
    	//convert to byte[]
    	payloadLen_b=ByteBuffer.allocate(4).putInt(payloadLen).array(); // +12 for the size of header?
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
