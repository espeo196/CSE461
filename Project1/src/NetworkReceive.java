import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Class for handling receiving network packets.
 * 
 * @author espeo
 *
 */
public class NetworkReceive {

	/**
	 * Prepares to receive data for stage A through the given socket.
	 * 
	 * @param socket the DatagramSocket used to send data.
	 */
	public void receiveStageA(DatagramSocket socket) {
		byte[] buffer = new byte[30]; // may need to change value
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				
		try {
			socket.receive(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO: extract information from the received information which is now stored in packet.
		buffer = packet.getData();
	}
}
