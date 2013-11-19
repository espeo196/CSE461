package Project2;

/**
 * Runs both the client and Server to test the server
 * @author benjamin
 *
 */
public class ServerRunner {
		
	public static void main(String[] args) {
		ServerMain server = new ServerMain();
		if(!server.stageA()){
			return ;
		}
		System.out.println("stage A passed");
		if(!server.stageB()){
			return ; 
		}
		System.out.println("stage B passed");
		
		server.displayStatus();
	}

}
