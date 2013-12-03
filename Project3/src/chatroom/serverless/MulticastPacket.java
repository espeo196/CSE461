/**
 * 
 */
package chatroom.serverless;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * @author benjamin
 * May not need this class if packets are simple enough, right now storing all the methods that are useful for processing byte[]
 * 
 * This class handles the message in a packet, adds headers to separate normal packets from ACK packets ( for connecting and disconnecting)
 */
public class MulticastPacket {
	private String content;
	private String senderName;
	private int size;			// has to be less than 1024byte?
	private int type;			//
	private static final int MAX_SIZE=1024; 	//use socket.getReceiveBufferSize()
	
	/**get packet from byte array
	 * packet structure
	 * type : 0 = ACK (online), 1 =  ACK (offline), 2 = normal
	 * count : need it only when long messages are split into few packets
	 * content : store in ASCII
	 *  0               1               2               3
 	 *	0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7
	 *	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *	|                        type                                   |
	 *	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *	|                        count                                  |
	 *	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *	|                        content                                |
	 *	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * @param packet packet receive
	 * @throws UnsupportedEncodingException 
	 */
	public MulticastPacket(byte[] packet) throws UnsupportedEncodingException{
		if(packet.length>MAX_SIZE){
			//error packet invalid
		}
		int type = byteArrayToInt(Arrays.copyOfRange(packet, 0, 4), 0);
		int count =  byteArrayToInt(Arrays.copyOfRange(packet, 4, 8), 0);
		String content= new String(Arrays.copyOfRange(packet,8, packet.length), "UTF-8");
		
	}
	/**create packet
	 * 
	 * @param sourceAddress
	 * @param soureceName
	 * @param content
	 */
	public MulticastPacket(String soureceName, String content, int maxPacketSize){
		
	}
	/**
	 *  create normal packet to transfer message
	 * @return
	 */
	public byte[] createPacket(){
		byte[] packet = new byte[MAX_SIZE];
		return packet;
	}
	
	/**
	 * creates ACK packets to acknowledge other client that it is connected or disconnected
	 * type == 1 , count == 1, content = name
	 * @return
	 */
	public byte[] createACK(){
		byte[] packet = new byte[MAX_SIZE];
		return packet;
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
	public String getSenderName(){
		return senderName;
	}
	public String getContent(){
		return content;
	}
	/** print values
	 * 
	 */
	public String toString(){
		return content;
		
	}
}
