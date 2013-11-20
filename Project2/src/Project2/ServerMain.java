package Project2;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;

/**
 * 
 * @author Nicholas Johnson, Benjamin Chan
 *
 */
public class ServerMain implements Runnable {
	
	public ServerValuesHolder values;
	
	public ServerMain(DatagramPacket packet) {
		byte[] studentID = Arrays.copyOfRange(packet.getData(), 10, 12);
		InetAddress senderAddress= packet.getAddress();
		int senderPort= packet.getPort();
		
		values = new ServerValuesHolder();
		values.setStudentID(studentID);
		values.setSenderAddress(senderAddress);
		values.setSenderPort(senderPort);
		values.setInitialPacket(Arrays.copyOfRange(packet.getData(),12,packet.getData().length));
	}
	
	@Override
	public void run() {

		if(!this.stageA()){
			return ;
		}
		System.out.println("stage A passed");
		if(!this.stageB()){
			return ; 
		}
		System.out.println("stage B passed");
		
		this.displayStatus();
		
	}
	/**
	 * Perform stage A
	 * receive a packet from client
	 * transmit a packet if it the client's packet is valid 
	 */
	public boolean stageA() {
		// establish server socket
		byte[] receivedData = values.getInitialPacket();		
		try {
			// verify header and that the received packet is long enough
			if(receivedData.length > ServerValuesHolder.HEADER_LENGTH && PacketVerifier.verifyStageA(receivedData, values)) {
				byte[] data = PacketCreater.stageAPacket(values);
				DatagramSocket socket = new DatagramSocket(ServerValuesHolder.udp_portInit);
				DatagramPacket sendPacket = new DatagramPacket(data, data.length, values.getSenderAddress(), values.getSenderPort());
				socket.send(sendPacket);
				socket.close();
				return true;
			}else{
				System.out.println("stage A packet not valid");
				return false;
			}
		} catch (IOException e) {
			System.out.println("IOException caught: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		
	}
	
	/**
	 * Perform stage B
	 * receive and acknowledge several packets.
	 */
	public boolean stageB() {
		try {
			Random rand = new Random();
			InetAddress senderAddress = null;
			int senderPort = 0;
			
			DatagramSocket socket = new DatagramSocket(values.getUdp_port());
			
			byte[] buffer = new byte[ServerValuesHolder.HEADER_LENGTH + values.getLen() + 4];
			
			for(int i = 0; i < values.getNum(); i++) {
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				// randomly determine whether to ACK or not
				if(rand.nextBoolean()) {					
					byte[] receivedData = packet.getData();
					senderAddress = packet.getAddress();
					senderPort = packet.getPort();

					// extract packet_id
					if(receivedData.length > ServerValuesHolder.HEADER_LENGTH && PacketVerifier.verifyStageB(receivedData, values, i)) {
					
						byte[] data = PacketCreater.stageBAck(values, i);
						DatagramPacket sendPacket = new DatagramPacket(data, data.length, senderAddress, senderPort);
						socket.send(sendPacket);
					} else {
						// malformed packet received
						System.out.println("Stage B malformed packet received");
						socket.close();
						return false;
					}
				} else {
					i--; // chose not to acknowledge. decrement counter
				}
			}
			
			if(senderAddress != null) {
				byte[] data = PacketCreater.stageBPacket(values);
				DatagramPacket sendPacket = new DatagramPacket(data, data.length, senderAddress, senderPort);
				socket.send(sendPacket);
			}		
			socket.close();
		} catch (IOException e) {
			System.out.println("IOException caught: " + e.getMessage());
			e.printStackTrace();
		}
		return true;
		
	}
	
	/**
	 * Perform stageC. Set up a tcp connection and send a response.
	 */
	public void stageC() {
		try {
			ServerSocket socket = new ServerSocket(values.getTcp_port());
			Socket connectionSocket = socket.accept();
			
			System.out.println("Server has connected.");
			byte[] data = PacketCreater.stageCPacket(values);
			
			DataOutputStream out = new DataOutputStream(connectionSocket.getOutputStream());
			out.write(data);
			
		} catch (IOException e) {
			System.out.println("IOException caught: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void displayStatus(){
		System.out.println(values.toString());
		ServerValuesHolder.printPacket(PacketCreater.stageAPacket(values), "----------------stage A packet----------------");
		ServerValuesHolder.printPacket(PacketCreater.stageBPacket(values), "----------------stage B packet----------------");
		ServerValuesHolder.printPacket(PacketCreater.stageCPacket(values), "----------------stage C packet----------------");
		//ServerValuesHolder.printPacket(PacketCreater.stageDPacket(values), "----------------stage D packet----------------");
	}
	

}
