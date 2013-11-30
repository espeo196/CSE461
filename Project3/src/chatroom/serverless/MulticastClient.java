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
	private MulticastSocket mcs;
	
	public MulticastClient(MulticastSocket mcs){
		this.mcs=mcs;
	}

	@Override
	public void run() {
		
		try {
			byte buf[] = new byte[1024];
			DatagramPacket pack = new DatagramPacket(buf, buf.length);
			while(true){
				mcs.receive(pack);
				System.out.println("Received data from: " + pack.getAddress().toString() +
						    ":" + pack.getPort() + " with length: " +
						    pack.getLength());
				System.out.write(pack.getData(),0,pack.getLength());
				System.out.println();
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}	
	}
}
