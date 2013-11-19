package Project2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.Random;

/**
 * Basic class for holding values that need to persist.
 *
 */
public class ServerValuesHolder{
	public static final int HEADER_LENGTH = 12;
	public static final int MIN_PORT = 49152;	//range of available port
	public static final int MAX_PORT = 65535;
	
	//Server values
	private int studentID;
	private InetAddress senderAddress;
	private int senderPort;
	
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
		senderAddress=null;
		senderPort=0;
		
		Random rand = new Random();
		payloadInit=stringToByte("hello world");

		len = rand.nextInt(20)+1;
		num = rand.nextInt(20)+1;
		len2 = rand.nextInt(20)+1;
		num2 = rand.nextInt(20)+1;
		
		secretInit=0;
		secretA = rand.nextInt();
		secretB = rand.nextInt();
		secretC = rand.nextInt();
		secretD = rand.nextInt();
		
		udp_portInit = 12235;
		udp_port = generateServerPort();
		tcp_port = generateServerPort();
		
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
	 * Set the sender Address
	 * @param senderAddress 
	 * @return true if address is not null
	 */
	public boolean setSenderAddress(InetAddress senderAddress){
		if(senderAddress!=null){
			this.senderAddress=senderAddress;
			return true;
		}else{
			return false;
		}
	}
	/**
	 * Set the sender port
	 * @param senderPort
	 * @return true if the port number is in range
	 */
	public boolean setSenderPort(int senderPort){
		if(senderPort >= MIN_PORT && senderPort <= MAX_PORT){
			this.senderPort=senderPort;
			return true;
		}else{
			return false;
		}
	}
	
	public int generateServerPort(){
		Random rand=new Random();
		int randPort = 0;
		int count = 0;
		boolean portAvailable = false;
		while(!portAvailable){
			randPort = rand.nextInt(MAX_PORT - MIN_PORT)+ MIN_PORT;
			portAvailable=checkPort(randPort);
			count++;
			if(count>= MAX_PORT - MIN_PORT){
				throw new RuntimeException("Cannot find available port");
			}
		}
		return randPort;
	}
	/**
	 * Checks if the port is available on this computer
	 *
	 * @param port the port to check
	 * @return true if the port is available
	 */
	private static boolean checkPort(int port) {
		ServerSocket serverSocket = null;
		DatagramSocket dataSocket = null;
		
		try {
			serverSocket = new ServerSocket(port );
			serverSocket.setReuseAddress(true );
			dataSocket = new DatagramSocket(port );
			dataSocket.setReuseAddress(true );
			return true;
			
		} catch (IOException e) {
			return false;
			
		} finally {
			if (dataSocket != null) {
				dataSocket.close();
			}
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
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
	    for (int i = 0; i < b.length; i++) {
	        int shift = (b.length - 1 - i) * 8;
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
	
	/**
	 * Print out the content of the packet
	 *
	 * @param packet byte[] to have its contents printed.
	 * @param title String to be shown before printing packet contents.
	 */
	public static void printPacket(byte[] packet, String title){
		if(title!=null)
			System.out.println(title);
		if(packet!=null){
			System.out.println("Packet Header:");
			printByteArray(packet, 0, 12);
			
			System.out.println("Packet Content:");
			printByteArray(packet, 12, packet.length);
			
			System.out.println("---------------");
		}else{
			System.out.println("null");
		}	
	}
	
	private static void printByteArray(byte[] packet, int offset, int length){
		for (int j= offset; j < length; j++) {
			System.out.format("0x%x ", packet[j]);
	 		if((j+1)%4 == 0) {
	 			System.out.println();
	        }
		}
	}
	
	@Override
	public String toString() {
		return "\n Server Connection Status :[ \n"+
				"\t"+"studentID=" + studentID + "\n"+
				"\t"+"secretInit="+ secretInit + "\n"+
				"\t"+"payloadInit=" + Arrays.toString(payloadInit)+ "\n"+
				"\t"+"udp_portInit=" + udp_portInit + "\n"+
				"\t"+"num=" + num + "\n"+
				"\t"+"len=" + len + "\n"+
				"\t"+"udp_port=" + udp_port + "\n"+ 
				"\t"+"secretA=" + secretA + "\n"+
				"\t"+"tcp_port=" + tcp_port + "\n"+
				"\t"+"secretB=" + secretB + "\n"+
				"\t"+"num2="+ num2 + "\n"+
				"\t"+"len2=" + len2 + "\n"+
				"\t"+"secretC=" + secretC + "\n"+
				"\t"+"c=" + c + "\n"+
				"\t"+"secretD=" + secretD + "]" + "\n" ;
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
	public byte[] getNum2_byte(){
		return intToByteArray(num2);
	}

	public int getLen2() {
		return len2;
	}
	
	public byte[] getLen2_byte(){
		return intToByteArray(len2);
	}

	public int getSecretC() {
		return secretC;
	}
	
	public byte[] getSecretC_byte(){
		return intToByteArray(secretC);
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
	
	public InetAddress getSenderAddress() {
		return senderAddress;
	}
	public int getSenderPort() {
		return senderPort;
	}
	
	

}
