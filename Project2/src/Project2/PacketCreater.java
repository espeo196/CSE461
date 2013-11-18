package Project2;

import java.nio.ByteBuffer;

/**
 * Creates packets for different stages
 *
 */
public class PacketCreater {
	public static final int HEADER_LENGTH = 12;
	
	/**
	 * Generate packet for stageA
	 * @param studentID 3 digit int in form of byte[].
	 * @param secretA int secret to include in the packet.
	 * @param num
	 * @param len
	 * @param udp_port
	 * @param secretA
	 * @return a byte[] containing the packet for stageA
	 */
	public static byte[] stageAPacket(ServerValuesHolder values) {		
		byte[] payload = new byte[16];
		payload[3] = (byte) values.getNum(); // num
		payload[7] = (byte) values.getLen(); // len
		System.arraycopy(values.getUdp_port_byte(), 0, payload, 8, 4); // udp_port
		System.arraycopy(values.getSecretA_byte(), 0, payload, 12, 4); // secretA
		
		return createPacket(values.getSecretInit(), 2, values.getStudentID(), payload);
	}
	
	/**
	 * Generates acknowledgement packet for stage B
	 * @param studentID 3 digit int converted into bytes
	 * @param id int packet_id of ACK
	 * @return
	 */
	public static byte[] stageBPacket(ServerValuesHolder values) {
		byte[] payload = new byte[8];
		System.arraycopy(values.getTcp_port_byte(), 0, payload, 0, 4);
		System.arraycopy(values.getSecretB_byte(), 0, payload, 4, 4);
		
		return createPacket(values.getSecretA(), 2, values.getStudentID(), payload);
	}
	/**
	 * Generates acknowledgement packet for stage B
	 * @param studentID 3 digit int converted into bytes
	 * @param id int packet_id of ACK
	 * @return
	 */
	public static byte[] stageBAck(ServerValuesHolder values,int id) {
		byte[] payload = new byte[4];
		payload[3] = (byte) id;
		return createPacket(values.getSecretA(), 1, values.getStudentID(), payload);
	}
	/**
	 * Creates a packet to be sent to the client in stage C.
	 * @param studentID 3 digit int converted into bytes
	 * @param secretC int secret to include in packet
	 * @return a byte[] containing the packet for stage C
	 */
	public static byte[] stageCPacket(ServerValuesHolder values) {
		byte[] payload = new byte[13];
		System.arraycopy(values.getNum2_byte(), 0, payload, 0, 4);
		System.arraycopy(values.getLen2_byte(), 0, payload, 4, 4);
		System.arraycopy(values.getSecretC_byte(), 0, payload, 8, 4);
		payload[12] = (byte) values.getC();
		
		return createPacket(values.getSecretC(), 2, values.getStudentID(), payload);
	}
	
	/**
	 * 
	 * @param psecret int secret to include in the packet.
	 * @param step int
	 * @param studentID 3 digit int.
	 * @param payload into data to include in the packet.
	 * @return a byte[] containing a header and payload.
	 */
	public static byte[] createPacket(int psecret, int step, int studentID, byte[] payload) {
		byte[] data = new byte[(int) (4*(Math.ceil((HEADER_LENGTH + payload.length)/4.0)))];
		byte[] header = createHeader(payload.length, psecret, step, studentID);
		
		System.arraycopy(header, 0, data, 0, header.length);
		System.arraycopy(payload, 0, data, header.length, payload.length);
		
		return data;
	}
	
	/**
	 * Creates the packet header with the following format:
	 *  0               1               2               3  
	 *	0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7
	 *	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *	|                           payload_len                         |
	 *	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *	|                             psecret                           |
	 *	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *	|             step               |  last 3 digits of student #  |
	 *	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 */
	private static byte[] createHeader(int payloadLen, int psecret, int step, int studentID) {
		byte[] header = new byte[HEADER_LENGTH];
		byte[] payloadLen_b = new byte[4];
		byte[] psecret_b = new byte[4];
		byte[] step_b = new byte[2];
		byte[] studentID_b=new byte[2];
		
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
