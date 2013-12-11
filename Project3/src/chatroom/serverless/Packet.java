package chatroom.serverless;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
/**
 * Packets for sending 
 * @author benjamin
 *
 */
public class Packet implements Comparable<Packet>{
	public static final int MAX_SIZE = 20;
	public static final int HEADER_LENGTH = 12;
	private byte[] content;
	private int count;			// order of packet in a Message  ( reverse order )
	private int type;			//
	private int id;
	
	/**get packet from byte array
	 * packet structure
	 * type : 0 = ACK (online), 1 =  ACK (offline), 2 = normal 3 = file
	 * count : order of packet in a message ( in reverse order)
	 * content : store in ASCII
	 *  0               1               2               3
 	 *	0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7
	 *	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *	|                        type                                   |
	 *	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *	|                        id                                     |
	 *	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *	|                        count                                  |
	 *	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *	|                        content                                |
	 *	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * @param packet packet receive
	 * @throws UnsupportedEncodingException 
	 */
	public Packet(byte[] packet) throws UnsupportedEncodingException{
		if(packet.length>MAX_SIZE){
			//error packet invalid
		}
		this.type = byteArrayToInt(Arrays.copyOfRange(packet, 0, 4), 0);
		this.id =  byteArrayToInt(Arrays.copyOfRange(packet, 4, 8), 0);
		this.count =  byteArrayToInt(Arrays.copyOfRange(packet, 8, 12), 0);
		this.content = Arrays.copyOfRange(packet, HEADER_LENGTH, packet.length);
	}
	/**create packet
	 * 
	 * @param sourceAddress
	 * @param soureceName
	 * @param content
	 */
	public Packet(int type, int id , int count, byte[] content){
		if(content.length>MAX_SIZE-HEADER_LENGTH){
			//error packet invalid
		}
		this.type = type;
		this.id = id;
		this.count = count;
		this.content = content;
	}
	/**
	 *  create byte array for sending transfer message
	 * @return
	 */
	public byte[] createPacket(){
		byte[] packet = new byte[content.length+HEADER_LENGTH];
		System.arraycopy(intToByteArray(type), 0, packet, 0, 4);
		System.arraycopy(intToByteArray(id), 0, packet, 4, 4);
		System.arraycopy(intToByteArray(count), 0, packet, 8, 4);
		System.arraycopy(content, 0, packet, 12, content.length);
		
		return packet;
	}
	
	/**
	 * creates ACK packets to acknowledge other client that it is connected or disconnected
	 * type == 1 , count == 1, content = name
	 * @return
	 */
	public static byte[] createACK(String name){
		byte[] nameB = stringToByte(name);
		Packet pk = new Packet(0, 0 , 1 , nameB);  
		return pk.createPacket();
	}
	
	public static byte[] createFIN(){
		byte[] nameB = stringToByte(" ");
		Packet pk = new Packet(1, 0 , 1 , nameB);  
		return pk.createPacket();
	}
	
	public int getType(){
		return type;
	}
	public int getCount(){
		return count;
	}
	public int getID(){
		return id;
	}
	public byte[] getContent(){
		return content;
	}
	public String getText() throws UnsupportedEncodingException{
		//TODO: set encoding
		return byteToString(content); 
	}
	
	/**
	 * Comparator 
	 * @param other
	 * @return
	 */
	@Override
	public int compareTo(Packet other){
		return other.count > this.count ? +1 : other.count < this.count ? -1 : 0;
	}

	/** print values
	 * 
	 */
	public String toString(){
		return null;
		
	}
	/**
	 * Convert String with ASCII encoding to byte[] 
	 * with length divisible by 4 
	 * with null terminator at back
	 * 
	 * @param str string to convert
	 * @return byte array represented 
	 * @throws UnsupportedEncodingException 
	 */
	private static byte[] stringToByte(String str) {
		if(str!=null) {
			byte[] byteArray;
			try {
				int length = str.getBytes("UTF-8").length;
				byteArray = new byte[length+1];
				System.arraycopy(str.getBytes("UTF-8"), 0, byteArray, 0,length );
				byteArray[length]=0;
				return byteArray;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return null;
			}
		}else
			return null;
	}
	private static String byteToString(byte[] byteArray) throws UnsupportedEncodingException{
		String value = new String(byteArray, "UTF-8");
		return value;
	}
	/**
	 * Convert the byte array to an int.
	 *
	 * @param b The byte array
	 * @param offset The array offset
	 * @return The integer
	 */
	private static int byteArrayToInt(byte[] b, int offset) {
	    int value = 0;
	    for (int i = 0; i < b.length; i++) {
	        int shift = (b.length - 1 - i) * 8;
	        value += (b[i + offset] & 0x000000FF) << shift;
	    }
	    return value;
	}
	/**
	 * Convert integer to byte array.
	 *
	 * @param value the integer
	 * @return The byte array
	 */
	private static byte[] intToByteArray(int value) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			int offset = (4 - 1 - i) * 8;
			b[i] = (byte) ((value >>> offset) & 0xFF);
		}
		return b;
	}



}
