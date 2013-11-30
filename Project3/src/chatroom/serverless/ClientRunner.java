package chatroom.serverless;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * 
 * @author Benjamin Chan, Nicholas Johnson
 *
 */

public class ClientRunner {
	public static final int inPort = 5000;
	public static final String group= "225.4.5.6";
	
	public static MulticastSocket mcs;
	public static void main(String[] args) {
		// TODO: runs intro UI stuff
		// TODO: runs receiving & UI on separate threads
	}
	//create socket, bind it to port and add to group
	public static void createReceiveSocket(){
		try {
			mcs = new MulticastSocket(inPort);
			mcs.joinGroup(InetAddress.getByName(group));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//print error message cannot create socket
			e.printStackTrace();
		}
		
	}
	//do this when quit:
	//s.leaveGroup(InetAddress.getByName(group));
	//s.close();
}
