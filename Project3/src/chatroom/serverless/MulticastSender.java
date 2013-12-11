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
public class MulticastSender {
	
	/**
	 * Send a message to a local group.
	 * 
	 * @param message String message to send.
	 * @param group String group to send to message to. Should be in the format 0.0.0.0
	 * @param port int port to use
	 * @throws IOException
	 */
	public static void sendMessage(Message msg, String group, int port) throws IOException {
		
		if(msg == null)
			return;
		
		// Create the socket but we don't bind it as we are only going to send data
		MulticastSocket s = new MulticastSocket();
		
		// Note that we don't have to join the multicast group if we are only
		// sending data and not receiving
		
		for(int i=0;i<msg.getSize();i++){
			// Create a DatagramPacket 
			//send packet
			byte[] buf = msg.getPacket(i).createPacket();
			DatagramPacket packet = new DatagramPacket(buf , buf.length,
								 InetAddress.getByName(group), port);
			// Do a send.
			s.send(packet);
		}
		// And when we have finished sending data close the socket
		s.close();
	}
	public static void sendMessage(String message, String group, int port) throws IOException {
		Message msg= new Message(message);
		sendMessage(msg,group,port);
	}
	public static void send(byte[] buf, String group, int port) throws IOException{
		// Create the socket but we don't bind it as we are only going to send data
		MulticastSocket s = new MulticastSocket();
		
		DatagramPacket packet = new DatagramPacket(buf , buf.length,
								 InetAddress.getByName(group), port);
		s.send(packet);
		
		s.close();
	}
}
