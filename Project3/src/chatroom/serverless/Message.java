/**
 * 
 */
package chatroom.serverless;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


/**
 * @author benjamin
 * May not need this class if packets are simple enough, right now storing all the methods that are useful for processing byte[]
 * 
 * This class handles the message in a packet, adds headers to separate normal packets from ACK packets ( for connecting and disconnecting)
 */
public class Message {
	private List<Packet> packets;
	private int id; 
	
	public Message(String content){
		Random rand = new Random();
		id = rand.nextInt();
		packets = new ArrayList<Packet>();
		createPackets(content);
	}
	public Message(Packet packet){
		id = packet.getID();
		packets = new ArrayList<Packet>();
		packets.add(packet);	
	}
	public void addPacket(Packet packet){
		packets.add(packet);
	}
	
	private void createPackets(String content){
		try {
			byte[] bytes = content.getBytes("UTF-8");
			int packetNum = (bytes.length/Packet.MAX_SIZE)+1;
			for(int i = 0; i < packetNum ;i++){
				int packetLength = (Packet.MAX_SIZE < bytes.length)? Packet.MAX_SIZE : bytes.length;
				// packet number is in descending order
				packets.add( new Packet( 2 , id, packetNum - i ,
						Arrays.copyOfRange(bytes,i*Packet.MAX_SIZE, i*Packet.MAX_SIZE + packetLength))
				);
			}
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//return number of packet
	public int getSize(){
		return packets.size();
	}
	public Packet getPacket(int id){
		return packets.get(id); 
	}

}
