package Project2;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * 
 * @author Nicholas Johnson, Benjamin Chan
 *
 */
public class ServerMain {

	public static final int HEADER_LENGTH = 12;
	
	public static void stageA() {
		// establish server socket
		try {
			DatagramSocket socket = new DatagramSocket(12235);
			
			byte[] buffer = new byte[HEADER_LENGTH + 4]; // header length + payload length = 12 + 4
			
			// receive request
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			socket.receive(packet);
			
			byte[] receivedData = packet.getData();
			byte[] studentID = new byte[4];
			// verify header
			if(PacketVerifier.stageA(receivedData)) {
				studentID=Arrays.copyOfRange(receivedData, 10, 12);
				byte[] data=PacketCreater.stageA();
				DatagramPacket sendPacket = new DatagramPacket(data, data.length, packet.getAddress(), packet.getPort());
				socket.send(sendPacket);
			}
			socket.close();
		} catch (SocketException e) {
			System.out.println("SocketException caught: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException caught: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
