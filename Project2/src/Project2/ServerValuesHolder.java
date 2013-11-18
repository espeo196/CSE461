package Project2;

import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * Basic class for holding values that need to persist.
 *
 */
public class ServerValuesHolder{
	public static final int HEADER_LENGTH = 12;
	
	//Server values
	private int studentID;
	
	//initial value
	private int secretInit;
	private byte[] payloadInit;
	private int udp_portInit;
	//stage A
	private int num;
	private int len;
	private int udp_port;
	private int secretA;
	//Stage B
	private int tcp_port;
	private int secretB;
	//Stage C
	private int num2;
	private int len2;
	private int secretC;
	private char c;
	
	private int secretD;
	
	public ServerValuesHolder() {
		studentID=0;
		
		Random rand = new Random();
		payloadInit=stringToByte("hello world");

		len = rand.nextInt(20);
		num = rand.nextInt(20);
		len2 = rand.nextInt(20);
		num2 = rand.nextInt(20);
		
		secretInit=0;
		secretA = generateSecret();
		secretB = generateSecret();
		secretC = generateSecret();
		secretD = generateSecret();
		
		// TODO: needs to be random and in correct valid range
		udp_portInit = 12235;
		tcp_port = 12235;
		udp_port = 12235;
	}
	/**
	 * Set the student id
	 * @param studentID
	 * @return true if the id is valid 
	 */
	public boolean setStudentID(byte[] studentID){
		if(studentID.length==2){
			this.studentID=byteArrayToInt(studentID,0);
			return true;
		}
		return false;
	}
	/**
	 * Randomly generates a 4 byte secret and returns it
	 * @return a 4 byte integer containing a randomly generated secret
	 */
	private int generateSecret() {
		Random random = new Random();
		int randomNum=random.nextInt(2147483647); //largest number in int
		return randomNum;
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
	private static byte[] stringToByte(String str){
		if(str!=null){
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
	/**
	 * Convert the byte array to an int.
	 *
	 * @param b The byte array
	 * @param offset The array offset
	 * @return The integer
	 */
	private static int byteArrayToInt(byte[] b, int offset) {
	    int value = 0;
	    for (int i = 0; i < 4; i++) {
	        int shift = (4 - 1 - i) * 8;
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
			int offset = (b.length - 1 - i) * 8;
			b[i] = (byte) ((value >>> offset) & 0xFF);
		}
		return b;
	}
	public int getUdp_portInit(){
		return udp_portInit;
	}
	public int getStudentID() {
		return studentID;
	}

	public int getSecretInit() {
		return secretInit;
	}

	public byte[] getPayloadInit() {
		return payloadInit;
	}

	public int getNum() {
		return num;
	}

	public int getLen() {
		return len;
	}
	
	public int getUdp_port() {
		return udp_port;
	}
	public byte[] getUdp_port_byte(){
		return intToByteArray(udp_port);
	}

	public int getSecretA() {
		return secretA;
	}
	public byte[] getSecretA_byte() {
		return intToByteArray(secretA);
	}

	public int getTcp_port() {
		return tcp_port;
	}
	public byte[] getTcp_port_byte() {
		return intToByteArray(tcp_port);
	}

	public int getSecretB() {
		return secretB;
	}
	public byte[] getSecretB_byte() {
		return intToByteArray(secretB);
	}

	public int getNum2() {
		return num2;
	}

	public int getLen2() {
		return len2;
	}

	public int getSecretC() {
		return secretC;
	}

	public char getC() {
		return c;
	}

	public int getSecretD() {
		return secretD;
	}

	public static int getHeaderLength() {
		return HEADER_LENGTH;
	}


}
