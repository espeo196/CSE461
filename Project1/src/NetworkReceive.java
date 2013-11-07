import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Class for handling receiving network packets.
 * 
 * @author Nicholas Johnson, Benjamin Chan
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
	public static byte[] listen(DatagramSocket socket, int timeout) throws IOException {
		byte[] buf = new byte[28];
		socket.setSoTimeout(timeout);
		try {
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
	        socket.receive(packet);
	        
	        return packet.getData();
	        
	    } catch (SocketTimeoutException e) {
	        System.out.println("timeout");
	    }
		return null; // no data to return
	}
	
	/**
	 * Set up a TCP socket and Listens for traffic from it
	 * Null is returned if a timeout occurs.
	 * 
	 * @param serverAddress the destination Server Address
	 * @param timeout int milliseconds before the socket expires
	 * @return a byte array containing the received data or null if a timeout occurs.
	 * @throws IOException
	 */
	public static byte[] listenTCP(Socket tcpSocket, int timeout) throws IOException {
		byte[] buf = new byte[28];
		char[] buf2= new char[28]; 
		tcpSocket.setSoTimeout(timeout);
		
		try {
			InputStreamReader reader= new InputStreamReader(tcpSocket.getInputStream());
			reader.read(buf2, 0, buf.length);
	 	    for (int i = 0; i < buf.length; i++) {
	 		   buf[i] = (byte) buf2[i];
	 	    }
	        return buf;
	        
	    } catch (SocketTimeoutException e) {
	        System.out.println("timeout");
	    }
		return null; // no data to return
	}	
}
