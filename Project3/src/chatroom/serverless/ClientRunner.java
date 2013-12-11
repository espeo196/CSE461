package chatroom.serverless;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Primary runner for the serverless chatroom. Each client runs an instance of this class.
 * The chatroom uses a text-based interface and listens passively printing messages as they
 * are received.
 * 
 * @author Benjamin Chan, Nicholas Johnson
 */

public class ClientRunner {
	public static final int IN_PORT = 8888;
	
	public static String GROUP = "225.4.5.14";
	public static boolean runThreads = true;
	public static String username = "";
	public static List<String> userList = new ArrayList<String>(); 
	
	public static void main(String[] args) {		
		System.setProperty("java.net.preferIPv4Stack", "true");
		MulticastSocket mcs = null;
		
		// set the GROUP based on location
//		try {
//			GROUP = (InetAddress.getLocalHost()).getHostAddress();
//		} catch (UnknownHostException e) {
//			System.out.println("Exception when getting local host: " + e.getMessage());
//			e.printStackTrace();
//		}
		
		// instantiates the MulticastSocket and joins the group
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