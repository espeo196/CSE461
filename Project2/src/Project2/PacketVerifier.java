package Project2;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Verify packets received from client
 * @author benjamin
 *
 */
public class PacketVerifier {
	public static final int HEADER_LENGTH = 12;
	
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
	public static boolean verifyHeader(byte[] data, int payloadLen, int psecret, int step, int studentID) {
		byte[] payloadLenByte = new byte[4];
		byte[] psecretByte = new byte[4];
		byte[] stepByte = new byte[2];
		byte[] studentIDByte = new byte[2];
		
		payloadLenByte = ByteBuffer.allocate(4).putInt(payloadLen).array();
		psecretByte = ByteBuffer.allocate(4).putInt(psecret).array();
		stepByte = ByteBuffer.allocate(4).putInt(step).array();
		studentIDByte = ByteBuffer.allocate(4).putInt(studentID).array();
		
		// make sure the payload length, secret, step, and studentID are correctly 
		// allocated in the header.
		if(!compareArrays(Arrays.copyOfRange(data, 0, 4), payloadLenByte)) {
			return false;
		} else if(!compareArrays(Arrays.copyOfRange(data, 4, 4), psecretByte)) {
			return false;
		} else if(!compareArrays(Arrays.copyOfRange(data, 8, 2), stepByte)) {
			return false;
		} else if(!compareArrays(Arrays.copyOfRange(data, 10, 2), studentIDByte)) {
			return false;
		}
		return true;
		
	}
	
	/**
	 * Compare two arrays
	 * @param array1
	 * @param array2
	 * @return true when they contain the same value
	 */
	private static boolean compareArrays(byte[] array1, byte[] array2){
		if ((array1 == null && array2 == null) || (array1 == null && array2 != null) 
												|| (array1 != null && array2 == null)) {
			return false;
		} else if (array1.length != array2.length) {
			return false;
		}
		
		for (int i = 0; i < array2.length; i++) {
			if (array2[i] != array1[i]) {
				return false;
			}                 
		}
		return true;
	}
	
	/**
	 * Verify a Packet. This includes verifying that the header is in the correct format.
	 * @param packet			received packet
	 * @param expectedPayload	expected payload content
	 * @param psecret			expected psecret
	 * @param step				expected step of the stage
	 * @param studentID			expected studentID
	 * @return
	 */
	private static boolean verifyPacket(byte[] packet, byte[] expectedPayload, int psecret, int step, int studentID) {
		if(packet.length != expectedPayload.length + HEADER_LENGTH) {
			return false;
		}
		byte[] header = Arrays.copyOfRange(packet, 0, HEADER_LENGTH);
		byte[] payload = Arrays.copyOfRange(packet, HEADER_LENGTH, packet.length);
		
		// verify header
		if(!verifyHeader(header, payload.length, psecret, step, studentID)){
			return false;
		}
		// verify payload content
		else if(!compareArrays(expectedPayload, payload)) {
			return false;
		}
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
	 * @return true if the packet is valid
	 */
	public static boolean verifyStageA(byte[] packet, int studentID, int psecret) {
		byte[] expectedPayload = new byte[4];
		System.arraycopy("hello world".getBytes(), 0, expectedPayload, 0, 4); // TODO: need null terminator
		if(packet.length > 12) {
			return verifyPacket(packet, expectedPayload, psecret, 1, studentID);
		}
		return false;
	}
	
	public static boolean verifyStageB(byte[] receivedData, int studentID,
			int secretA, int i) {

		byte[] zeroPayload = new byte[ServerMain.values.lenA];
		for(int j = 0; j < zeroPayload.length; j++) {
			zeroPayload[j] = 0;
		}
		
		byte[] expectedPayload = new byte[4 + ServerMain.values.lenA];
		System.arraycopy((byte) i, 0, expectedPayload, 0, 4);
		System.arraycopy(zeroPayload, 0, expectedPayload, 4, zeroPayload.length);
		
		if(receivedData.length > 12 && ServerMain.byteArrayToInt(expectedPayload, 12) == i) {
			return verifyPacket(receivedData, expectedPayload, secretA, 1, studentID);
		}
		return false;
	}
	
	public static boolean stageC (byte[] packet){
		
		return false;
	}
	
	public static boolean stageD (byte[] packet){
		
		return false;
	}
}
