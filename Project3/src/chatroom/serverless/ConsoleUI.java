package chatroom.serverless;

import java.io.IOException;
import java.util.Date;
import java.util.Scanner;

/**
 * 
 * @author Benjamin Chan, Nicholas Johnson
 *
 */
public class ConsoleUI implements Runnable {
	public static final String EXIT_STRING = "exit";
	private String username = "";
	Scanner console = new Scanner(System.in);
	
	@Override
	public void run() {
		try {
			printIntro(console);
			getInput(console);
		} catch (IOException e) {
			System.out.println("IOException occurred: " + e.getMessage());
			e.printStackTrace();
		}
		console.close();
		printOutro();
	}

	public void printIntro(Scanner console) throws IOException	{
		System.out.println("**********************************************************");
		System.out.println("Welcome to the serverless chatroom.");
		System.out.println("You will be connected to the chatroom for your local area.");
		System.out.println("You will be connected to the chatroom for your local area.");
		System.out.println("**********************************************************");
	
		
		System.out.println();
		System.out.println("What's your username? ");
		username = console.nextLine();
	
		MulticastSender.send(Packet.createACK(username), ClientRunner.GROUP, ClientRunner.IN_PORT);
		
	}
	
	/**
	 * Waits for user to type something and sends the message to the group.
	 * Exits when the user types "exit" case insensitive.
	 * @throws IOException 
	 */
	public void getInput(Scanner console) throws IOException {
		String message = "";
		System.out.println("Type \"exit\" to exit the chatroom.");
		
		while(ClientRunner.runThreads) {
			message = console.nextLine();
			
			if(!message.equalsIgnoreCase(EXIT_STRING)) {
				MulticastSender.sendMessage(message, ClientRunner.GROUP, ClientRunner.IN_PORT);
			} else {
				MulticastSender.send(Packet.createFIN(), ClientRunner.GROUP, ClientRunner.IN_PORT);
				ClientRunner.runThreads = false;
			}
		}
		
	}
	
	public void printOutro()	{
		System.out.println("**********************************************************");
		System.out.println("Goodbye.");
	}
	
	/**
	 * Print out message receied
	 * @param sender
	 * @param content
	 */
	public static void printReceive(String sender, String content){
		Date date = new Date( );
		System.out.printf("%1$s : %2$s [ %3$tT %3$tm/%3$td]\n", 
                sender , content, date);
	}

}
