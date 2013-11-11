package Project2;

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
	public static boolean verifyHeader(byte[] data,int payload_len, byte[] psecret, int step, byte[] studentID ) {
		// TODO verify Header by comparing data with actual numbers
		return false;
		
	}
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
	
	private static boolean verifyPacket(byte[] packet,byte[] actualContent,byte[] psecret,int step, byte[] studentID){
		if(packet.length!=actualContent.length+HEADER_LENGTH){
			return false;
		}
		byte[] header=Arrays.copyOfRange(packet, 0, HEADER_LENGTH);
		byte[] payload=Arrays.copyOfRange(packet, HEADER_LENGTH, packet.length);
		
		//verify header
		if(!verifyHeader(header,actualContent.length,psecret,step,studentID)){
			return false;
		}
		//verify payload content
		if(!compareArrays(actualContent,payload))
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
	public static boolean stageA(byte[] packet){
		byte[] payload = "hello world".getBytes();//need null terminator
		byte[] psecret = new byte[4];
		byte[] studentID= new byte[2];
		if(packet.length>12){
			studentID=Arrays.copyOfRange(packet, 10, 12);
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
