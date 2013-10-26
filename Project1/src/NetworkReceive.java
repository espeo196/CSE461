import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Class for handling receiving network packets.
 * 
 * @author espeo
 *
 */
public class NetworkReceive {

        public static void listen(DatagramSocket socket) throws IOException{
                byte[] buf = new byte[16];
                socket.setSoTimeout(1000);
                try {
                        DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
         
                // display response
//                String received = new String(packet.getData(), 0, packet.getLength());
                int i=1;
                System.out.println("Data received:");
                for (byte b : packet.getData()) {
                             System.out.format("0x%x ", b);
                           if(i%4==0) System.out.println();
                             i++;
                          }
            }catch (SocketTimeoutException e) {
                System.out.println("timeout");
            }
        }
}