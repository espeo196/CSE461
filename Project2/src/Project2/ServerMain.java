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
	//stores all the variable used by the server
	private ServerValuesHolder values;
	//stores the result of different stages for display.
	
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
	private boolean stageA() {
		// establish server socket
		byte[] receivedData = values.getInitialPacket();		
		try {
			printPacket(receivedData, "----------------received stage A packet----------------");
			// verify header and that the received packet is long enough
			if(receivedData.length > ServerValuesHolder.HEADER_LENGTH && PacketVerifier.verifyStageA(receivedData, values)) {
				byte[] data = PacketCreater.stageAPacket(values);
				DatagramPacket sendPacket = new DatagramPacket(data, data.length, values.getSenderAddress(), values.getSenderPort());
				values.getInitialSocket().send(sendPacket);
				printPacket(data, "Sent stage A packet to student: "+values.getStudentID()
						+" at: "+values.getInitialSocket().getInetAddress().getHostAddress()
						+" : "+values.getInitialSocket().getPort());
				return true;
			}else{
				System.out.println("stage A malformed packet received");
				return false;
			}
		} catch (IOException e) {
			System.out.println("IOException caught: " + e.getMessage());
			return false;
		}
		
	}
	
	/**
	 * Perform stage B
	 * receive and acknowledge several packets.
	 */
	private boolean stageB() {
		try {
			byte[] buffer = new byte[(int) (4*(Math.ceil((ServerValuesHolder.HEADER_LENGTH + values.getLen()+4)/4.0)))];
			Random rand = new Random();
			DatagramSocket socket = new DatagramSocket(values.getUdp_port());
			socket.setSoTimeout(ServerValuesHolder.TIMEOUT);
			
			
			for(int i = 0; i < values.getNum(); i++) {
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				
				// randomly determine whether to ACK or not
				if(rand.nextBoolean()&&verifySender(packet)) {					
					byte[] receivedData = packet.getData();
					printPacket(receivedData, "----------------received stage B packet----------------");
					// extract packet_id
					if(receivedData.length > ServerValuesHolder.HEADER_LENGTH && PacketVerifier.verifyStageB(receivedData, values, i)) {
					
						byte[] data = PacketCreater.stageBAck(values, i);
						DatagramPacket sendPacket = new DatagramPacket(data, data.length, values.getSenderAddress(), values.getSenderPort());
						socket.send(sendPacket);
						printPacket(data, "Sent stage B ACK packet to student: "+values.getStudentID()
								+" at: "+values.getSenderAddress().getHostAddress()
								+" : "+values.getSenderPort());
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
			printPacket(data, "Sent stage B packet to student: "+values.getStudentID()
					+" at: "+values.getSenderAddress().getHostAddress()
					+" : "+values.getSenderPort());
			socket.close();
		} catch (IOException e) {
			System.out.println("IOException caught: " + e.getMessage());
			return false;
		}
		return true; // success!
	}
	
	/**
	 * Perform stage C. 
	 * Set up a tcp connection and send a response.
	 */
	private boolean stageC() {
		
		try {
			ServerSocket socket = new ServerSocket(values.getTcp_port());
			socket.setSoTimeout(ServerValuesHolder.TIMEOUT);
			Socket connectionSocket = socket.accept();
			
			System.out.println("Server has connected.");
			byte[] data = PacketCreater.stageCPacket(values);
			printPacket(data, "Sent stage C packet to student: "+values.getStudentID()
								+" at: "+values.getSenderAddress().getHostAddress()
								+" : "+values.getSenderPort());
			DataOutputStream out = new DataOutputStream(connectionSocket.getOutputStream());
			out.write(data);
			
			values.setTcpSocket(socket);
			values.setTcpConnectionSocket(connectionSocket);
			return true;
		} catch (IOException e) {
			System.out.println("IOException caught: " + e.getMessage());
			return false;
		}
	}
	
	/**
	 * Perform stage D. 
	 * Receive several payloads before responding.
	 */
	private boolean stageD() {
		
		try {
			ServerSocket socket = values.getTcpSocket();
			Socket connectionSocket = values.getTcpConnectionSocket();
			socket.setSoTimeout(ServerValuesHolder.TIMEOUT);
			
			DataInputStream in = new DataInputStream(connectionSocket.getInputStream());
			
			byte[] buffer = new byte[(int) (4*(Math.ceil((ServerValuesHolder.HEADER_LENGTH + values.getLen2())/4.0)))];			
			
			for(int i = 0; i < values.getNum2(); i++) {
				in.read(buffer);
				
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
			printPacket(data, "Sent stage D packet to student: "+values.getStudentID()
					+" at: "+values.getSenderAddress().getHostAddress()
					+" : "+values.getSenderPort());
			socket.close();
		} catch (IOException e) {
			System.out.println("IOException caught: " + e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * Displays the desired content of packets for stages A-D for testing/debugging.
	 */
	public void displayStatus(){
		System.out.println(values.toString());
		printPacket(PacketCreater.stageAPacket(values), "----------------Sent stage A packet----------------");
		printPacket(PacketCreater.stageBPacket(values), "----------------Sent stage B packet----------------");
		printPacket(PacketCreater.stageCPacket(values), "----------------Sent stage C packet----------------");
		printPacket(PacketCreater.stageDPacket(values), "----------------Sent stage D packet----------------");
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
	
	/**
	 * Print out the content of the packet
	 *
	 * @param packet byte[] to have its contents printed.
	 * @param title String to be shown before printing packet contents.
	 */
	public static void printPacket(byte[] packet, String title) {
		if(title!=null)
			System.out.println(title);
		if(packet!=null) {
			System.out.println("Packet Header:");
			printByteArray(packet, 0, 12);
			
			System.out.println("Packet Content:");
			printByteArray(packet, 12, packet.length);
			
			System.out.println("---------------");
		}else{
			System.out.println("null");
		}	
	}
	/**
	 * print out a the content of a byte array
	 * Print out 4 bytes in the array on the same line
	 * @param packet
	 * @param offset
	 * @param length
	 */
	private static void printByteArray(byte[] packet, int offset, int length) {
		for (int j= offset; j < length; j++) {
			System.out.format("0x%x ", packet[j]);
	 		if((j+1)%4 == 0) {
	 			System.out.println();
	        }
		}
	}
}
