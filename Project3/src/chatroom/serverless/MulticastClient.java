package chatroom.serverless;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.Arrays;

/**
 * 
 * @author Benjamin Chan, Nicholas Johnson
 *
 */
public class MulticastClient implements Runnable {
	// TODO: start listening passively
	// TODO: send out message that this user is on the network
	// TODO: handle received messages and print them\
	// TODO: keep track of clients on the same network
	// Which port should we listen to
	private MulticastSocket mcs;
	
	public MulticastClient(MulticastSocket mcs) {
		this.mcs = mcs;
	}

	@Override
	public void run() {
		// TODO: create a multicastPacket and display the content
		try {
			byte buf[] = new byte[1024];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			while(true) {
				mcs.receive(packet);
				//process packet
				byte[] data = packet.getData();
				int type = byteArrayToInt(Arrays.copyOfRange(data, 0, 4), 0);
				int count =  byteArrayToInt(Arrays.copyOfRange(data, 4, 8), 0);
				
				String content= new String(Arrays.copyOfRange(data,8, packet.getLength()), "UTF-8");
				
				System.out.println("Received data from: " + packet.getAddress().toString() +
						    ":" + packet.getPort() + " with length: " +
						    packet.getLength());
				System.out.write(packet.getData(), 0, packet.getLength());
				System.out.println();
			}
			
		} catch (IOException e) {
			System.out.println("IOException while receiving: " + e.getMessage());
			e.printStackTrace();
		}	
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

}
