package Project2;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Verify packets received from client
 * @author benjamin
 *
 */

public class PacketVerifier {
	public static final int HEADER_LENGTH=12;
	/**
	 * Verifies whether the received header is in the correct format as follows:
	 * 
	 *  0               1               2               3  
 	 *	0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7
	 *	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *	|                           payload_len                         |
	 *	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *	|                             psecret                           |
	 *	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *	|             step               |  last 3 digits of student #  |
	 *	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *
	 * @param data byte[] to be verified. Note that this data contains the header and payload.
	 * @return true if the header if formatted correctly, false otherwise
	 */
	public static boolean verifyHeader(byte[] data,int payloadLen, int psecret, int step, int studentID ) {
		byte[] payloadLenByte = new byte[4];
		byte[] psecretByte=new byte[4];
		byte[] stepByte = new byte[2];
		byte[] studentIDByte=new byte[2];
		
		payloadLenByte=ByteBuffer.allocate(4).putInt(payloadLen).array();
		psecretByte=ByteBuffer.allocate(4).putInt(psecret).array();
		stepByte=ByteBuffer.allocate(4).putInt(step).array();
		studentIDByte=ByteBuffer.allocate(4).putInt(studentID).array();
		
		if(!compareArrays(Arrays.copyOfRange(data, 0, 4),payloadLenByte)){
			return false;
		}
		
		if(!compareArrays(Arrays.copyOfRange(data, 4, 4),psecretByte)){
			return false;
		}
		
		if(!compareArrays(Arrays.copyOfRange(data, 8, 2),stepByte)){
			return false;
		}

		if(!compareArrays(Arrays.copyOfRange(data, 10, 2),studentIDByte)){
			return false;
		}
		return false;
		
	}
	/**
	 * Compare two arrays
	 * @param array1
	 * @param array2
	 * @return true when they contain the same value
	 */
	private static boolean compareArrays(byte[] array1, byte[] array2){
		if (array1 == null && array2 == null)
			return false;

		if (array1.length != array2.length)
			return false;
        
		for (int i = 0; i < array2.length; i++) {
			if (array2[i] != array1[i]) {
				return false;
			}                 
		}
		
		return true;
	}
	/**
	 * Verify a Packet 
	 * @param packet			received packet
	 * @param payloadContent	expected payload content
	 * @param psecret			expected psecret
	 * @param step				expected step of the stage
	 * @param studentID			expected studentID
	 * @return
	 */
	private static boolean verifyPacket(byte[] packet,byte[] payloadContent,int psecret,int step, int studentID){
		if(packet.length!=payloadContent.length+HEADER_LENGTH){
			return false;
		}
		byte[] header=Arrays.copyOfRange(packet, 0, HEADER_LENGTH);
		byte[] payload=Arrays.copyOfRange(packet, HEADER_LENGTH, packet.length);
		
		//verify header
		if(!verifyHeader(header,payload.length,psecret,step,studentID)){
			return false;
		}
		//verify payload content
		if(!compareArrays(payloadContent,payload))
			return false;
		
		return true;
	}

	/**
	 * Verifies whether packet from stage A ( header and payload)
	 *  payload of stage A
	 *  0               1               2               3
	 *	0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7
	 *	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *	|                                                               |
	 *	|                        hello world                            |
	 *	|                                                               |
	 *	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * @return true when the packet is valid
	 */
	public static boolean stageA(byte[] packet,int studentID,int psecret){
		byte[] payload = new byte[4];
		System.arraycopy("hello world".getBytes(), 0, payload, 0, 4);//need null terminator
		if(packet.length>12){
			return verifyPacket(packet, payload,psecret,1,studentID);
		}
		return false;
	}
	
	public static boolean stageB (byte[] packet){
		
		return false;
	}
	
	public static boolean stageC (byte[] packet){
		
		return false;
	}
	
	public static boolean stageD (byte[] packet){
		
		return false;
	}
}
