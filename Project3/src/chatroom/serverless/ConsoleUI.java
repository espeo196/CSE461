package chatroom.serverless;

import java.io.IOException;
import java.util.Scanner;

/**
 * 
 * @author Benjamin Chan, Nicholas Johnson
 *
 */
public class ConsoleUI implements Runnable {
	public static final String EXIT_STRING = "exit";
	
	@Override
	public void run() {
		printIntro();
		
		getInput();
		
		printOutro();
	}

	public static void printIntro()	{
		System.out.println("**********************************************************");
		System.out.println("Welcome to the serverless chatroom.");
		System.out.println("You will be connected to the chatroom for your local area.");
		System.out.println("You will be connected to the chatroom for your local area.");
		System.out.println("**********************************************************");
	}
	
	/**
	 * Waits for user to type something and sends the message to the group.
	 * Exits when the user types "exit" case insensitive.
	 */
	public void getInput() {
		Scanner console = new Scanner(System.in);
		String message = "";
		System.out.println("Type \"exit\" to exit the chatroom.");
		
		while(!message.equalsIgnoreCase(EXIT_STRING)) {
			message = console.nextLine();
			
			if(!message.equalsIgnoreCase(EXIT_STRING)) {
				try {
					MulticastSender.send(message, ClientRunner.GROUP, ClientRunner.IN_PORT);
				} catch (IOException e) {
					System.out.println("IOException when sending message: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		console.close();
		// TODO: quit out of the chatroom and stop both threads
	}
	
	public static void printOutro()	{
		System.out.println("**********************************************************");
		System.out.println("Goodbye.");
	}

}
