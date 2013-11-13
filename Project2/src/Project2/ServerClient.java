package Project2;

import Project1.NetworkMain;
import Project1.NetworkReceive;
import Project1.NetworkSend;

/**
 * Runs both the client and Server to test the server
 * @author benjamin
 *
 */
public class ServerClient {
		
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ServerMain server = new ServerMain();
		server.stageA();
	}

}
