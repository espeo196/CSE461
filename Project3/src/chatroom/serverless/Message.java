package chatroom.serverless;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


/** 
 * This class handles the message in a packet, adds headers to separate normal packets from ACK packets ( for connecting and disconnecting)
 * 
 * @author Benjamin Chan, Nicholas Johnson
 */
public class Message {
	private List<Packet> packets;
	private int id;
	private InetAddress senderAddress;
	
	public Message(){
		Random rand = new Random();
		id = rand.nextInt();
		packets = new ArrayList<Packet>();
		senderAddress = ClientRunner.address;
	}
	public Message(String content) throws UnknownHostException  {
		this();
		createPackets(content);
	}
	public Message(Packet packet , InetAddress senderAddress) {
		this.senderAddress = senderAddress;
		id = packet.getID();
		packets = new ArrayList<Packet>();
		packets.add(packet);	
	}
	public void addPacket(Packet packet) {
		packets.add(packet);
	}
	
	/**
	 * Splits the given content into a list of packets.
	 * 
	 * @param content String to be stored in the packets
	 */
	private void createPackets(String content) {
		try {
			byte[] bytes = content.getBytes("UTF-8");
			int packetNum = (bytes.length / Packet.MAX_SIZE) + 1;
			
			for(int i = 0; i < packetNum; i++) {
				int packetLength = (Packet.MAX_SIZE < bytes.length) ? Packet.MAX_SIZE : bytes.length;
				// packet number is in descending order
				packets.add( new Packet(2, id, packetNum - i,
						Arrays.copyOfRange(bytes, i*Packet.MAX_SIZE, i*Packet.MAX_SIZE + packetLength))
				);
			}
			
		} catch (UnsupportedEncodingException e) {
			System.out.println("UnsupportedEncoding when creating packets: " + e.getMessage());
			e.printStackTrace();
		}
	}
	//return number of packet
	public int getSize() {
		return packets.size();
	}
	public Packet getPacket(int id) {
		return packets.get(id); 
	}
	public int getID() {
		return id;
	}
	public InetAddress getSenderAddress(){
		return senderAddress;
	}

}
