import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

/**
 * Class for handling receiving network packets.
 * 
 *
 */
public class NetworkReceive {

	/**
	 * Listens for traffic through the given socket and returns any received data.
	 * Null is returned if a timeout occurs.
	 * 
	 * @param socket DatagramSocket to listen on
	 * @return a byte array containing the received data or null if a timeout occurs.
	 * @throws IOException
	 */
	public static byte[] listen(DatagramSocket socket, int timeout) throws IOException{
		byte[] buf = new byte[28];
		socket.setSoTimeout(timeout);
		try {
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
	        socket.receive(packet);
	 	        
	        // Print received data for debugging purposes
	        NetworkMain.printPacket(buf, "Data received:");
	        return packet.getData();
	        
	    } catch (SocketTimeoutException e) {
	        System.out.println("timeout");
	    }
		return null; // no data to return
	}	
}
