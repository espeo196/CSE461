package Project2;
import java.io.DataInputStream;
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
 * Main server. Sequentially runs stages A-D.
 * @author Nicholas Johnson, Benjamin Chan
 *
 */
public class ServerMain implements Runnable {
	
	public ServerValuesHolder values;
	
	/**
	 * Constructor that sets up initial values including starting
	 * packet and socket information.
	 * @param packet DatagramPacket containing initial data
	 * @param socket DatagramSocket used at the start of server use
	 */
	public ServerMain(DatagramPacket packet, DatagramSocket socket) {
		byte[] studentID = Arrays.copyOfRange(packet.getData(), 10, 12);
		InetAddress senderAddress= packet.getAddress();
		int senderPort= packet.getPort();
		
		values = new ServerValuesHolder();
		values.setStudentID(studentID);
		values.setSenderAddress(senderAddress);
		values.setSenderPort(senderPort);
		values.setInitialPacket(Arrays.copyOfRange(packet.getData(),0,packet.getData().length));
		values.setInitialSocket(socket);
	}
	
	@Override
	public void run() {
		this.displayStatus();
		if(!this.stageA()) {
			return ;
		}
		System.out.println("stage A passed");
		if(!this.stageB()) {
			return ; 
		}
		System.out.println("stage B passed");
		if(!this.stageC()) {
			return ; 
		}
		System.out.println("stage C passed");
		if(!this.stageD()) {
			return ;
		}
		System.out.println("stage D passed");
		
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
			ServerValuesHolder.printPacket(receivedData, "----------------received stage A packet----------------");
			// verify header and that the received packet is long enough
			if(receivedData.length > ServerValuesHolder.HEADER_LENGTH && PacketVerifier.verifyStageA(receivedData, values)) {
				byte[] data = PacketCreater.stageAPacket(values);
				DatagramPacket sendPacket = new DatagramPacket(data, data.length, values.getSenderAddress(), values.getSenderPort());
				values.getInitialSocket().send(sendPacket);
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
			byte[] buffer = new byte[(int) (4*(Math.ceil((ServerValuesHolder.HEADER_LENGTH + values.getLen())/4.0)))];
			Random rand = new Random();
			DatagramSocket socket = new DatagramSocket(values.getUdp_port());
			socket.setSoTimeout(ServerValuesHolder.TIMEOUT);
			
			
			for(int i = 0; i < values.getNum(); i++) {
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				
				// randomly determine whether to ACK or not
				if(rand.nextBoolean()&&verifySender(packet)) {					
					byte[] receivedData = packet.getData();
					ServerValuesHolder.printPacket(receivedData, "----------------received stage B packet----------------");
					// extract packet_id
					if(receivedData.length > ServerValuesHolder.HEADER_LENGTH && PacketVerifier.verifyStageB(receivedData, values, i)) {
					
						byte[] data = PacketCreater.stageBAck(values, i);
						DatagramPacket sendPacket = new DatagramPacket(data, data.length, values.getSenderAddress(), values.getSenderPort());
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
			
			byte[] data = PacketCreater.stageBPacket(values);
			DatagramPacket sendPacket = new DatagramPacket(data, data.length, values.getSenderAddress(), values.getSenderPort());
			socket.send(sendPacket);
			socket.close();
		} catch (IOException e) {
			System.out.println("IOException caught: " + e.getMessage());
			e.printStackTrace();
		}
		return true; // success!
	}
	
	/**
	 * Perform stage C. 
	 * Set up a tcp connection and send a response.
	 */
	public boolean stageC() {
		
		try {
			ServerSocket socket = new ServerSocket(values.getTcp_port());
			socket.setSoTimeout(ServerValuesHolder.TIMEOUT);
			Socket connectionSocket = socket.accept();
			
			System.out.println("Server has connected.");
			byte[] data = PacketCreater.stageCPacket(values);
			ServerValuesHolder.printPacket(data, "----------------sent stage C packet----------------");
			DataOutputStream out = new DataOutputStream(connectionSocket.getOutputStream());
			out.write(data);
			
			values.setTcpSocket(socket);
			values.setTcpConnectionSocket(connectionSocket);
			return true;
		} catch (IOException e) {
			System.out.println("IOException caught: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Perform stage D. 
	 * Receive several payloads before responding.
	 */
	public boolean stageD() {
		
		try {
			ServerSocket socket = values.getTcpSocket();
			Socket connectionSocket = values.getTcpConnectionSocket();
			socket.setSoTimeout(ServerValuesHolder.TIMEOUT);
			
			DataInputStream in = new DataInputStream(connectionSocket.getInputStream());
			
			byte[] buffer = new byte[(int) (4*(Math.ceil((ServerValuesHolder.HEADER_LENGTH + values.getLen2())/4.0)))];			
			
			for(int i = 0; i < values.getNum2(); i++) {
				in.read(buffer);
				ServerValuesHolder.printPacket(buffer, "----------------received stage D packet----------------");
				if(buffer.length <= ServerValuesHolder.HEADER_LENGTH || !PacketVerifier.verifyStageD(buffer, values)) {
					// malformed packet received
					System.out.println("Stage D malformed packet received");
					socket.close();
					return false;
				}
			}
			
			byte[] data = PacketCreater.stageDPacket(values);
			DataOutputStream out = new DataOutputStream(connectionSocket.getOutputStream());
			out.write(data);
			socket.close();
		} catch (IOException e) {
			System.out.println("IOException caught: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Displays the desired content of packets for stages A-D for testing/debugging.
	 */
	public void displayStatus(){
		System.out.println(values.toString());
		ServerValuesHolder.printPacket(PacketCreater.stageAPacket(values), "----------------stage A packet----------------");
		ServerValuesHolder.printPacket(PacketCreater.stageBPacket(values), "----------------stage B packet----------------");
		ServerValuesHolder.printPacket(PacketCreater.stageCPacket(values), "----------------stage C packet----------------");
		ServerValuesHolder.printPacket(PacketCreater.stageDPacket(values), "----------------stage D packet----------------");
	}
	
	/**
	 * Verifies that the packet came from a legal senderAddress
	 * @param packet received from a sender
	 * @return true if the packet was received through
	 * 		a bad address or port.
	 */
	private boolean verifySender(DatagramPacket packet){
		InetAddress senderAddress = packet.getAddress();
		int senderPort = packet.getPort();
		
		// boolean zen!
		return values.getSenderAddress().equals(senderAddress) && values.getSenderPort() == senderPort;
	}
}
