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
	
	@Override
	public void run() {
		try {
			printIntro();
			getInput();
		} catch (IOException e) {
			System.out.println("IOException occurred: " + e.getMessage());
			e.printStackTrace();
		}
				
		printOutro();
	}

	public void printIntro() throws IOException	{
		System.out.println("**********************************************************");
		System.out.println("Welcome to the serverless chatroom.");
		System.out.println("You will be connected to the chatroom for your local area.");
		System.out.println("You will be connected to the chatroom for your local area.");
		System.out.println("**********************************************************");
	
		Scanner console = new Scanner(System.in);
		System.out.println();
		System.out.println("What's your username? ");
		username = console.nextLine();
	
		MulticastSender.send(username + " has joined the chatroom.", ClientRunner.GROUP, ClientRunner.IN_PORT);
		console.close();
	}
	
	/**
	 * Waits for user to type something and sends the message to the group.
	 * Exits when the user types "exit" case insensitive.
	 * @throws IOException 
	 */
	public void getInput() throws IOException {
		Scanner console = new Scanner(System.in);
		String message = "";
		System.out.println("Type \"exit\" to exit the chatroom.");
		
		while(ClientRunner.runThreads) {
			message = username + ": " + console.nextLine();
			
			if(!message.equalsIgnoreCase(EXIT_STRING)) {
				MulticastSender.send(message, ClientRunner.GROUP, ClientRunner.IN_PORT);
			} else {
				ClientRunner.runThreads = false;
			}
		}
		console.close();
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
		System.out.printf("%1$s : %2$s [ %3$tT %3$tm/%3$td]", 
                sender , content, date);
	}

}
