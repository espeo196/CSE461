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
	 * @param secretA int secret to include in the packet.
	 * @param studentID 3 digit int converted into bytes
	 * @return a byte[] containing the packet for stageA
	 */
	public static byte[] stageAPacket(byte[] studentID, int secretA) {		
		byte[] payload = new byte[16];
		payload[3] = (byte) ServerMain.values.numA; // num
		payload[7] = (byte) ServerMain.values.lenA; // len
		System.arraycopy(intToByteArray(ServerMain.values.udp_portA), 0, payload, 8, 4); // udp_port
		System.arraycopy(intToByteArray(secretA), 0, payload, 12, 4); // secretA
		
		return createPacket(secretA, 2, studentID, payload);
	}

	/**
	 * Generate packet for stage B, step 2
	 * @param studentID 3 digit int converted into bytes
	 * @param secretB int secret to include in the packet
	 * @return
	 */
	public static byte[] stageBPacket(byte[] studentID, int secretB) {
		byte[] payload = new byte[8];
		System.arraycopy(intToByteArray(ServerMain.values.tcp_portB), 0, payload, 0, 4);
		System.arraycopy(intToByteArray(secretB), 0, payload, 4, 4);
		
		return createPacket(secretB, 2, studentID, payload);
	}

	/**
	 * Generates acknowledgement packet for stage B
	 * @param studentID 3 digit int converted into bytes
	 * @param id int packet_id of ACK
	 * @return
	 */
	public static byte[] stageBAck(byte[] studentID, int id) {
		byte[] payload = new byte[4];
		payload[3] = (byte) id;
		return createPacket(ServerMain.values.secretA, 1, studentID, payload);
	}
	

	/**
	 * Creates a packet to be sent to the client in stage C.
	 * @param studentID 3 digit int converted into bytes
	 * @param secretC int secret to include in packet
	 * @return a byte[] containing the packet for stage C
	 */
	public static byte[] stageCPacket(byte[] studentID, int secretC) {
		byte[] payload = new byte[13];
		System.arraycopy(intToByteArray(ServerMain.values.numC), 0, payload, 0, 4);
		System.arraycopy(intToByteArray(ServerMain.values.lenC), 0, payload, 4, 4);
		System.arraycopy(intToByteArray(secretC), 0, payload, 8, 4);
		payload[12] = (byte) ServerMain.values.c;
		
		return createPacket(ServerMain.values.secretC, 2, studentID, payload);
	}
	
	/**
	 * 
	 * @param psecret int secret to include in the packet.
	 * @param step int
	 * @param studentID 3 digit int.
	 * @param payload into data to include in the packet.
	 * @return a byte[] containing a header and payload.
	 */
	public static byte[] createPacket(int psecret, int step, byte[] studentID, byte[] payload) {
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
	private static byte[] createHeader(int payloadLen, int psecret, int step, byte[] studentID) {
		byte[] header = new byte[HEADER_LENGTH];
		byte[] payloadLen_b = new byte[4];
		byte[] psecret_b = new byte[4];
		byte[] step_b = new byte[2];
		
		//convert to byte[]
		payloadLen_b=ByteBuffer.allocate(4).putInt(payloadLen).array();
		psecret_b=ByteBuffer.allocate(4).putInt(psecret).array();
		step_b=ByteBuffer.allocate(4).putInt(step).array();
		
		//copy to header
		System.arraycopy(payloadLen_b, 0, header, 0, 4);
		System.arraycopy(psecret_b, 0, header, 4, 4);
		System.arraycopy(step_b, 2, header, 8, 2);
		System.arraycopy(studentID, 2, header, 10, 2);
		
		return header;
	}
	
	public static byte[] intToByteArray(int value) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			int offset = (b.length - 1 - i) * 8;
			b[i] = (byte) ((value >>> offset) & 0xFF);
		}
		return b;
	}
}
