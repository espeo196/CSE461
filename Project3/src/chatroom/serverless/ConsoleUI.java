package chatroom.serverless;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
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
	public void run(){
		try {
			printIntro(console);
			getInput(console);
			printOutro();
		} catch (IOException e) {
			System.out.println("IOException occurred: " + e.getMessage());
			e.printStackTrace();
		} catch (InterruptedException e){
			e.printStackTrace();
		}
		console.close();
	}

	/**
	 * Prints introductory message including a list of valid commands.
	 * Also, sends a message stating that the user has joined the chatroom.
	 * 
	 * @throws IOException if there's a problem sending the username ACK.
	 * @throws InterruptedException 
	 */
	public void printIntro(Scanner console) throws IOException, InterruptedException	{
		System.out.println("**********************************************************");
		System.out.println("Welcome to the serverless chatroom.");
		System.out.println("You will be connected to the chatroom for your local area.");
		System.out.println("**********************************************************\n");
		
		printCommands();
		
		System.out.println();
		System.out.println("What's your username? ");
		String name = console.nextLine();
		ClientRunner.initiate(name);
		ClientRunner.updateUsers();
		//Thread.sleep(1000);
		//printUsers();
	}
	
	/**
	 * Waits for user to type something and sends the message to the group.
	 * Exits when the user types "exit" case insensitive.
	 * 
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public void getInput(Scanner console) throws IOException, InterruptedException {
		String message = null;
		
		while(ClientRunner.runThreads) {
			message = console.nextLine();
			
			if(message != null && !message.isEmpty() && !message.trim().equals("") 
					&& !message.trim().equals("\n")) {
				// if user typed a command
				if(message.trim().substring(0, 1).equals("/")) {
					//exit
					if(message.trim().equalsIgnoreCase(EXIT_STRING)) {
						ClientRunner.runThreads = false;
						MulticastSender.send(Packet.createFIN(), ClientRunner.GROUP, ClientRunner.IN_PORT);
						
					//help
					} else if(message.trim().equalsIgnoreCase(HELP_STRING)) {
						printCommands();
					//users
					} else if(message.trim().equalsIgnoreCase(USERS_STRING)) {
						ClientRunner.updateUsers();
						//wait for users to response
						Thread.sleep(1000);
						printUsers();
					//file transfer
					} else if(message.trim().startsWith("/file") && message.split(" ").length == 2) {
						Message msg = FileProcessor.read(message.split(" ")[1]);
						MulticastSender.sendMessage(msg, ClientRunner.GROUP, ClientRunner.IN_PORT);
					//invalid commands
					} else {
						System.out.println("Invalid command. Valid commands are:\n ");
						printCommands();
					}
				} else {
					MulticastSender.sendMessage(message, ClientRunner.GROUP, ClientRunner.IN_PORT);
				}
			}
		}	
	}
	
	private void printCommands() {
		System.out.println("Commands: ");
		System.out.println("'/exit' to leave the chatroom.");
		System.out.println("'/users' to see a list of joined users.");
		System.out.println("'/file filename' to send a file");
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
	public static void printReceive(String sender, String content) {
		Date date = new Date();
		System.out.printf("%3$s [%2$tT %2$tm/%2$td] : %1$s \n", 
                content, date, sender);
	}
	
	public static void printReceive(Message message) throws UnsupportedEncodingException {
		Date date = new Date();
		String username = ClientRunner.userList.get(message.getSenderAddress());
		System.out.printf("%2$s [%1$tT %1$tm/%1$td] : ", date , username);
		for(int i=0; i < message.getSize(); i++) {
			System.out.print(message.getPacket(i).getText());
		}
		System.out.println();
	}
	
	public static void printUsers(){
		if(ClientRunner.userList.isEmpty()){
			System.out.println("no active users");
		}else{
			System.out.println("Users: ");
			for(InetAddress useradd : ClientRunner.userList.keySet()) {
				System.out.println("\t" + useradd.toString() +":"+ ClientRunner.userList.get(useradd));
			}
		}
	}

}
