/**
 * Main class for handling UDP/TCP sending and receiving. 
 * @author espeo
 *
 */
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;

public class NetworkMain {
        public static final String SERVER_NAME = "bicycle.cs.washington.edu";
        public static final int PORT = 12235;
        public static DatagramSocket socket;
        public static InetAddress serverAddress;
        public static void main(String args[]) throws IOException {
                setup();
                NetworkSend.sendStageA(socket,serverAddress,PORT);
                NetworkReceive.listen(socket);
        }
        public static void setup(){
                try {
                        socket = new DatagramSocket();
                        serverAddress = InetAddress.getByName(SERVER_NAME);
                } catch (SocketException | UnknownHostException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
        }
}