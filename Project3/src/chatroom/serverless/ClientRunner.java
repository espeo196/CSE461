package chatroom.serverless;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 * 
 * @author Benjamin Chan, Nicholas Johnson
 *
 */

public class ClientRunner {
	public static final int IN_PORT = 8888;
	
//	public static final String GROUP = "225.4.5.6";
	public static String GROUP = "225.4.5.6";
	public static boolean runThreads = true;
	
	
	public static void main(String[] args) {		
		MulticastSocket mcs = null;
//		try {
//			GROUP = (InetAddress.getLocalHost()).getHostAddress();
//		} catch (UnknownHostException e) {
//			System.out.println("Exception when getting local host: " + e.getMessage());
//			e.printStackTrace();
//		}
		
		try {
			mcs = new MulticastSocket(IN_PORT);
			mcs.joinGroup(InetAddress.getByName(GROUP));
			
		} catch (IOException e) {
			System.out.println("Cannot create socket: " + e.getMessage());
			e.printStackTrace();
		}
		
		// runs receiving & UI on separate threads
		MulticastClient client = new MulticastClient(mcs);
		ConsoleUI ui = new ConsoleUI();
		
		Thread t1 = new Thread(client);
		Thread t2 = new Thread(ui);
		t1.start();
		t2.start();
	}

	/**
	 * Instantiates a MulticastSocket and binds it to the 
	 * local group.
	 * 
	 * @param mcs MulticastSocket to be instantiated
	 */
	public static void createReceiveSocket(MulticastSocket mcs) {
		try {
			mcs = new MulticastSocket(IN_PORT);
			mcs.joinGroup(InetAddress.getByName(GROUP));
			
		} catch (IOException e) {
			System.out.println("Cannot create socket: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Called when the user is leaving the chatroom. Exits the group
	 * and closes the connection.
	 * 
	 * @param mcs MulticastSocket to be closed
	 */
	public static void leaveChatroom(MulticastSocket mcs) {
		try {
			mcs.leaveGroup(InetAddress.getByName(GROUP));
			mcs.close();		
		} catch (IOException e) {
			System.out.println("Exception when attempting to leave chatroom: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
