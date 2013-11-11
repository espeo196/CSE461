package Project2;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;

/**
 * Creates Packets 
 * @author benjamin
 *
 */
public class PacketCreater {
	public static final int HEADER_LENGTH=12;
	
	public static byte[] stageA(){
		byte[] data = new byte[(int) (4*(Math.ceil((HEADER_LENGTH + 16 + 4)/4.0)))];
		byte[] header = createHeader(16, receivedData[4], 1, 856);
		byte[] payload = new byte[16];
		
		// TODO: should all be randomly generated and put in their correct places
		payload[3] = 5;
		payload[7] = 10;
		payload[11] = (byte) 22222;
		byte[] secret = generateSecret();
		System.arraycopy(secret, 0, payload, 12, secret.length);
		
		// copy the header, packet id, and payload into the same buffer to be sent.
		System.arraycopy(header, 0, data, 0, header.length);
		System.arraycopy(payload, 0, data, header.length, payload.length);
		
		
		return null;
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
	private static byte[] createHeader(int payloadLen, int psecret, int step, int student) {
    	byte[] header = new byte[HEADER_LENGTH];
    	byte[] payloadLen_b = new byte[4];
    	byte[] psecret_b = new byte[4];
    	byte[] step_b = new byte[2];
    	byte[] student_b = new byte [2];
    	
    	//convert to byte[]
    	payloadLen_b=ByteBuffer.allocate(4).putInt(payloadLen).array();
    	psecret_b=ByteBuffer.allocate(4).putInt(psecret).array();
    	step_b=ByteBuffer.allocate(4).putInt(step).array();
    	student_b=ByteBuffer.allocate(4).putInt(student).array();
    	
    	//copy to header
    	System.arraycopy(payloadLen_b,0,header,0,4);
    	System.arraycopy(psecret_b,0,header,4,4);
    	System.arraycopy(step_b,2,header,8,2);
    	System.arraycopy(student_b, 2, header, 10, 2);
    	
    	return header;
	}
	
	/**
	 * Randomly generates a 4 byte secret and returns it
	 * @return byte[] of length 4 containing a randomly generated secret
	 */
	private static byte[] generateSecret() {
		// TODO: should be randomly generated
		byte[] secret = {0, 1, 2, 3};
		
		return secret;
	}
}
