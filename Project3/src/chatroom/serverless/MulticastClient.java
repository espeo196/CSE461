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
public class MulticastClient implements Runnable{
	// TODO: start listening passively
	// TODO: send out message that this user is on the network
	// TODO: handle received messages and print them\
	// Which port should we listen to
	public static int port = 5000;
	// Which address
	public static String group = "225.4.5.6";
	
	public MulticastClient(){
		
	}
	
	public void run(){
		try{
			// Create the socket and bind it to port 'port'.
			MulticastSocket s = new MulticastSocket(port);
			// join the multicast group
			s.joinGroup(InetAddress.getByName(group));
			// Now the socket is set up and we are ready to receive packets
			// Create a DatagramPacket and do a receive
			byte buf[] = new byte[1024];
			DatagramPacket pack = new DatagramPacket(buf, buf.length);
			s.receive(pack);
			// Finally, let us do something useful with the data we just received,
			// like print it on stdout :-)
			System.out.println("Received data from: " + pack.getAddress().toString() +
					    ":" + pack.getPort() + " with length: " +
					    pack.getLength());
			System.out.write(pack.getData(),0,pack.getLength());
			System.out.println();
			// And when we have finished receiving data leave the multicast group and
			// close the socket
			s.leaveGroup(InetAddress.getByName(group));
			s.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	
}
