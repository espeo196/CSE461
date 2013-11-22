package Project2;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Verify packets received from client
 * @author Benjamin Chan, Nicholas Johnson
 *
 */
public class PacketVerifier {
	
	/**
	 * <pre>
	 * Verifies whether the received header is in the correct format as follows:
	 *  0               1               2               3  
 	 *  0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7
	 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *  |                           payload_len                         |
	 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *  |                             psecret                           |
	 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *  |             step               |  last 3 digits of student #  |
	 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * </pre>
	 * @param data byte[] to be verified. Note that this data contains the header and payload.
	 * @return true if the header if formatted correctly, false otherwise
	 */
	public static boolean verifyHeader(byte[] data, int payloadLen, int psecret, int step, int studentID) {
		byte[] payloadLenByte = ByteBuffer.allocate(4).putInt(payloadLen).array();
		byte[] psecretByte = ByteBuffer.allocate(4).putInt(psecret).array();
		byte[] stepByte = ByteBuffer.allocate(4).putInt(step).array();
		byte[] studentIDByte = ByteBuffer.allocate(4).putInt(studentID).array();
		
		// make sure the payload length, secret, step, and studentID are correctly 
		// allocated in the header.
		if(!compareArrays(Arrays.copyOfRange(data, 0, 4), Arrays.copyOfRange(payloadLenByte, 0, 4)))
			return false;
		if(!compareArrays(Arrays.copyOfRange(data, 4, 8), Arrays.copyOfRange(psecretByte, 0, 4)))
			return false;
		if(!compareArrays(Arrays.copyOfRange(data, 8, 10), Arrays.copyOfRange(stepByte, 2 ,4)))
			return false;
		if(!compareArrays(Arrays.copyOfRange(data, 10, 12), Arrays.copyOfRange(studentIDByte, 2 ,4)))
			return false;
		
		return true;
	}
	
	/**
	 * Compare two arrays
	 * @param array1
	 * @param array2
	 * @return true when they contain the same value or both are null
	 */
	private static boolean compareArrays(byte[] array1, byte[] array2) {
		if(array1 == null && array2 == null) {
			return true;
		} else if ((array1 == null && array2 != null) || (array1 != null && array2 == null)) {
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
		int expectedLength=(int) (4*(Math.ceil((expectedPayload.length + ServerValuesHolder.HEADER_LENGTH)/4.0)));
		if(packet.length != expectedLength) {
			return false;
		}
		byte[] header = Arrays.copyOfRange(packet, 0, ServerValuesHolder.HEADER_LENGTH);
		byte[] payload = Arrays.copyOfRange(packet, ServerValuesHolder.HEADER_LENGTH, expectedPayload.length+ServerValuesHolder.HEADER_LENGTH);
		
		// verify header
		if(!verifyHeader(header, expectedPayload.length, psecret, step, studentID)) {
			return false;
		}
		// verify payload content
		else if(!compareArrays(expectedPayload, payload)) {
			return false;
		}
		return true;
	}

	/**
	 * <pre>
	 * Verifies whether packet from stage A (header and payload)
	 *  payload of stage A
	 *  0               1               2               3
	 *  0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7
	 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *  |                                                               |
	 *  |                        hello world                            |
	 *  |                                                               |
	 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * </pre>
	 * @param values ServerValuesHolder containing commonly used values
	 * @return true if the packet is valid
	 */
	public static boolean verifyStageA(byte[] packet, ServerValuesHolder values) {
		return verifyPacket(packet, ServerValuesHolder.payloadInit, ServerValuesHolder.secretInit, 1,values.getStudentID());
	}
	
	/**
	 * <pre>
	 * Verifies the packet from stage B (header and payload)
	 * payload of stage B
	 *  0               1               2               3
 	 *  0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7
 	 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *  |                         packet_id                             |
	 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *  |                                                               |
	 *  |                   payload of length len                       |
	 *  |                                                               |
	 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *  </pre>
	 * @param receivedData byte[] containing the payload and header to be verified
	 * @param values ServerValuesHolder containing commonly used values
	 * @param packet_id int that should be the expected packet_id
	 * @return true if received data contains the correct information in its payload and header
	 */
	public static boolean verifyStageB(byte[] receivedData, ServerValuesHolder values, int packet_id) {
		// create dummmy payload to test. Should be all 0's

		byte[] expectedPayload = new byte[values.getLen()+4];
		
		for(int i = 0; i < expectedPayload.length; i++) {
			expectedPayload[i] = 0x0;
		}
		byte[] id = ByteBuffer.allocate(4).putInt(packet_id).array();
		System.arraycopy(id, 0, expectedPayload, 0, 4);

		
		return verifyPacket(receivedData, expectedPayload, values.getSecretA(), 1, values.getStudentID());
	}
	
	/**
	 * <pre>
	 * Verifies the payloads from stage D.
	 * payload of stage D
	 *  0               1               2               3
	 *  0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7
	 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *  |                                                               |
	 *  |           payload of length len2 filled with char c           |
	 *  |                                                               |
	 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *  </pre>
	 * @param receivedData byte[] containing payload and header to be verified
	 * @param values ServerValuesHolder containing commonly used values
	 * @return true if received data contains the correct information in its payload and header
	 */
	public static boolean verifyStageD(byte[] receivedData, ServerValuesHolder values) {
		// create dummy payload to test. Should be all character C.
		byte[] characterPayload = new byte[values.getLen2()];
		for(int i = 0; i < characterPayload.length; i++) {
			characterPayload[i] = (byte) values.getC();
		}
				
		return verifyPacket(receivedData, characterPayload, values.getSecretC(), 1, values.getStudentID());
	}
}