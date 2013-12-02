/**
 * 
 */
package chatroom.serverless;

import java.io.UnsupportedEncodingException;

/**
 * @author benjamin
 * This class handles the message in a packet, adds headers to separate normal packets from ACK packets ( for connecting and disconnecting)
 */
public class MulticastPacket {
	private String content;
	private String senderName;
	private int size;			// has to be less than 1400byte?
	private int type;			//ACK or normal
	private int maxPacketSize; 	//use socket.getReceiveBufferSize()
	/**get packet from byte array
	 * 
	 * @param packet packet receive
	 */
	public MulticastPacket(byte[] packet){
		
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
		byte[] packet = new byte[maxPacketSize];
		return packet;
	}
	
	/**
	 * creates ACK packets to acknowledge other client that it is connected or disconnected
	 * @return
	 */
	public byte[] createACK(){
		byte[] packet = new byte[maxPacketSize];
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
				byteArray = new byte[(int) (4*(Math.ceil((str.getBytes("US-ASCII").length+1)/4.0)))];
				System.arraycopy(str.getBytes("US-ASCII"), 0, byteArray, 0,str.getBytes("US-ASCII").length );
				byteArray[byteArray.length-1]=0;
				return byteArray;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return null;
			}
		}else
			return null;
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
