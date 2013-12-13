package chatroom.serverless;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
	public static InetAddress address ;
	//public static List<String> userList = new ArrayList<String>();
	//map address to name
	public static Map<InetAddress, String> userList = new HashMap<InetAddress, String>();
	
	public static void main(String[] args) {		
		System.setProperty("java.net.preferIPv4Stack", "true");
		MulticastSocket mcs = null;
		
		try {
			address = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			System.out.println("can't find address");
			e1.printStackTrace();
		}
		
		
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
	
	/**
	 * Update the user list
	 * @throws InterruptedException 
	 */
	public static void updateUsers() throws InterruptedException{
		try {
			userList.clear();
			Thread.sleep(500);
			MulticastSender.send(Packet.createAlive(), 
					ClientRunner.GROUP, ClientRunner.IN_PORT);
		} catch (IOException e) {
			System.out.println("Exception when attempting to update users: " + e.getMessage());
			e.printStackTrace();
		}
	}
	/** 
	 * send initiate signal
	 * @throws UnknownHostException 
	 */
	public static void initiate(String name) throws UnknownHostException{
		username = name;
		userList.put(address, name);
		try {
			MulticastSender.send(Packet.createACK(name), GROUP, IN_PORT);
			//MulticastSender.send(Packet.createAlive(), GROUP, IN_PORT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}