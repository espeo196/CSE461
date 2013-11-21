package Project2;

import java.nio.ByteBuffer;

/**
 * Creates UDP packets and TCP payloads for different stages
 * @author Benjamin Chan, Nicholas Johnson
 */
public class PacketCreater {
	
	/**
	 * Generate packet for stageA
	 * @param values ServerValuesHolder containing commonly used values
	 * @return a byte[] containing the packet for stage A
	 */
	public static byte[] stageAPacket(ServerValuesHolder values) {		
		byte[] payload = new byte[ServerValuesHolder.HEADER_LENGTH + 4]; 
		System.arraycopy(values.getNum_byte(), 0, payload, 0, 4);
		System.arraycopy(values.getLen_byte(), 0, payload, 4, 4);
		System.arraycopy(values.getUdp_port_byte(), 0, payload, 8, 4); // udp_port
		System.arraycopy(values.getSecretA_byte(), 0, payload, 12, 4); // secretA
		
		return createPacket(ServerValuesHolder.secretInit, 2, values.getStudentID(), payload);
	}
	
	/**
	 * Generates packet for stage B
	 * @param values ServerValuesHolder containing commonly used values
	 * @return a byte[] containing the packet for stage B
	 */
	public static byte[] stageBPacket(ServerValuesHolder values) {
		byte[] payload = new byte[8];
		System.arraycopy(values.getTcp_port_byte(), 0, payload, 0, 4);
		System.arraycopy(values.getSecretB_byte(), 0, payload, 4, 4);
		
		return createPacket(values.getSecretA(), 2, values.getStudentID(), payload);
	}
	/**
	 * Generates acknowledgement packet for stage B
	 * @param values ServerValuesHolder containing commonly used values
	 * @param id int packet_id of ACK
	 * @return a byte[] containing the ACK for stage B
	 */
	public static byte[] stageBAck(ServerValuesHolder values, int id) {
		byte[] payload = new byte[4];
		payload[3] = (byte) id;
		return createPacket(values.getSecretA(), 1, values.getStudentID(), payload);
	}
	/**
	 * Creates a packet to be sent to the client in stage C.
	 * @param values ServerValuesHolder containing commonly used values
	 * @return a byte[] containing the packet for stage C
	 */
	public static byte[] stageCPacket(ServerValuesHolder values) {
		byte[] payload = new byte[13];
		System.arraycopy(values.getNum2_byte(), 0, payload, 0, 4);
		System.arraycopy(values.getLen2_byte(), 0, payload, 4, 4);
		System.arraycopy(values.getSecretC_byte(), 0, payload, 8, 4);
		payload[12] = (byte) values.getC();
		
		return createPacket(values.getSecretB(), 2, values.getStudentID(), payload);
	}
	
	/**
	 * Creates a packet to be sent to the client in stage D.
	 * @param values ServerValuesHolder containing commonly used values
	 * @return a byte[] containing the packet for stage D
	 */
	public static byte[] stageDPacket(ServerValuesHolder values) {
		byte[] payload = values.getSecretD_byte();
		
		return createPacket(values.getSecretC(), 2, values.getStudentID(), payload);
	}
	/**
	 * Creates a byte array containing a header and payload.
	 * See @createHeader for more details on what the header contains
	 * 
	 * @param psecret int secret to include in the packet.
	 * @param step int
	 * @param studentID 3 digit int.
	 * @param payload byte[] data to include in the packet.
	 * @return a byte[] containing a header and payload.
	 */
	public static byte[] createPacket(int psecret, int step, int studentID, byte[] payload) {
		byte[] data = new byte[(int) (4*(Math.ceil((ServerValuesHolder.HEADER_LENGTH + payload.length)/4.0)))];
		byte[] header = createHeader(payload.length, psecret, step, studentID);
		
		System.arraycopy(header, 0, data, 0, header.length);
		System.arraycopy(payload, 0, data, header.length, payload.length);
		
		return data;
	}
	
	/**
	 * <pre>
	 * Creates the packet header with the following format:
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
	 *
	 * @param payloadLen int length of payload to be included with this packet
	 * @param psecret int secret to include in the packet.
	 * @param step int
	 * @param studentID 3 digit int.
	 * @return a byte[] containing the header formatted as above
	 */
	private static byte[] createHeader(int payloadLen, int psecret, int step, int studentID) {
		byte[] header = new byte[ServerValuesHolder.HEADER_LENGTH];
		byte[] payloadLen_b = new byte[4];
		byte[] psecret_b = new byte[4];
		byte[] step_b = new byte[2];
		byte[] studentID_b = new byte[2];
		
		//convert to byte[]
		payloadLen_b=ByteBuffer.allocate(4).putInt(payloadLen).array();
		psecret_b=ByteBuffer.allocate(4).putInt(psecret).array();
		step_b=ByteBuffer.allocate(4).putInt(step).array();
		studentID_b=ByteBuffer.allocate(4).putInt(studentID).array();
		
		//copy to header
		System.arraycopy(payloadLen_b, 0, header, 0, 4);
		System.arraycopy(psecret_b, 0, header, 4, 4);
		System.arraycopy(step_b, 2, header, 8, 2);
		System.arraycopy(studentID_b, 2, header, 10, 2);
		
		return header;
	}
}