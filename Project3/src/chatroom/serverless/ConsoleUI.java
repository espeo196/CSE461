package chatroom.serverless;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Scanner;

/**
 * Main UI class. Is entirely console based.
 * 
 * @author Benjamin Chan, Nicholas Johnson
 */
public class ConsoleUI implements Runnable {
	public static final String EXIT_STRING = "/exit";
	public static final String USERS_STRING = "/users";
	public static final String HELP_STRING = "/help";
	Scanner console = new Scanner(System.in);
	
	@Override
	public void run() {
		try {
			printIntro(console);
			getInput(console);
			printOutro();
		} catch (IOException e) {
			System.out.println("IOException occurred: " + e.getMessage());
			e.printStackTrace();
		}
		console.close();
	}

	/**
	 * Prints introductory message including a list of valid commands.
	 * Also, sends a message stating that the user has joined the chatroom.
	 * 
	 * @throws IOException if there's a problem sending the username ACK.
	 */
	public void printIntro(Scanner console) throws IOException	{
		System.out.println("**********************************************************");
		System.out.println("Welcome to the serverless chatroom.");
		System.out.println("You will be connected to the chatroom for your local area.");
		System.out.println("**********************************************************\n");
		
		printCommands();
		
		System.out.println();
		System.out.println("What's your username? ");
		ClientRunner.username = console.nextLine();
		ClientRunner.userList.add(ClientRunner.username);
		
		MulticastSender.send(Packet.createACK(ClientRunner.username), ClientRunner.GROUP, ClientRunner.IN_PORT);
		
	}
	
	/**
	 * Waits for user to type something and sends the message to the group.
	 * Exits when the user types "exit" case insensitive.
	 * 
	 * @throws IOException 
	 */
	public void getInput(Scanner console) throws IOException {
		String message = null;
		
		while(ClientRunner.runThreads) {
			message = console.nextLine();
			
			if(message != null && !message.isEmpty() && !message.trim().equals("") 
					&& !message.trim().equals("\n")) {
				// if user typed a command
				if(message.trim().substring(0, 1).equals("/")) {
					if(message.trim().equalsIgnoreCase(EXIT_STRING)) {
						ClientRunner.runThreads = false;
						MulticastSender.send(Packet.createFIN(ClientRunner.username), ClientRunner.GROUP, ClientRunner.IN_PORT);
					} else if(message.trim().equalsIgnoreCase(HELP_STRING)) {
						printCommands();
					} else if(message.trim().equalsIgnoreCase(USERS_STRING)) {
						System.out.println("Users: ");
						for(String user : ClientRunner.userList) {
							System.out.println("\t" + user);
						}
					} else if(message.trim().startsWith("/file") && message.split(" ").length == 2) {
						MulticastSender.sendMessage(ClientRunner.username + " is sending a file.", ClientRunner.GROUP, ClientRunner.IN_PORT);
						
						//transfer file
						Message msg = FileProcessor.read(message.split(" ")[1]);
						MulticastSender.sendMessage(msg, ClientRunner.GROUP, ClientRunner.IN_PORT);
					} else {
						System.out.println("Invalid command. Valid commands are:\n ");
						printCommands();
					}
				} else {
					MulticastSender.sendMessage(ClientRunner.username + ": " + message, ClientRunner.GROUP, ClientRunner.IN_PORT);
				}
			}
		}	
	}
	
	private void printCommands() {
		System.out.println("Commands: ");
		System.out.println("'/exit' to leave the chatroom.");
		System.out.println("'/users' to see a list of joined users.");
		System.out.println("'/file filename' to send a file with size < 5 MB.");
		System.out.println("'/help' to reprint the list of commands.");
		System.out.println("**********************************************************\n");
	}

	public void printOutro() throws IOException	{
		System.out.println("**********************************************************");
		System.out.println("Goodbye.");
	}
	
	/**
	 * Print out message received
	 * @param sender String name of the sender
	 * @param content
	 */
	public static void printReceive(String content) {
		Date date = new Date();
		System.out.printf("[%2$tT %2$tm/%2$td] : %1$s \n", 
                content, date);
	}
	
	public static void printReceive(Message message) throws UnsupportedEncodingException {
		Date date = new Date();
		System.out.printf(" [%1$tT %1$tm/%1$td] : ", date);
		for(int i=0; i < message.getSize(); i++) {
			System.out.print(message.getPacket(i).getText());
		}
		System.out.println();
	}

}
