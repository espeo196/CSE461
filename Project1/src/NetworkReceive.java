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
	public static byte[] listen(DatagramSocket socket) throws IOException{
		byte[] buf = new byte[16];
		socket.setSoTimeout(1000);
		try {
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
	        socket.receive(packet);
	 	        
	        // Print received data for debugging purposes
	        int i=1;
	        System.out.println("Data received:");
	        for (byte b : packet.getData()) {
	        	System.out.format("0x%x ", b);
	        	if(i%4==0) {
	        		System.out.println();
	        		i++;
	        	}
	  		}
	        return packet.getData();
	        
	    } catch (SocketTimeoutException e) {
	        System.out.println("timeout");
	    }
		return null; // no data to return
	}
}
