package chatroom.serverless;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class FileProcessor {

	public static Message read(String filepath){
		File f = new File(filepath);
//		if(!f.isFile()){
//			//print error
//			return null;
//		}
		
		byte[] b ;
		Packet p;
		Message msg = new Message();
		try {
			BufferedInputStream br = new BufferedInputStream(new FileInputStream(f));
			int count = (int) ((f.length()/Packet.MAX_SIZE)+1)+1; // first packet is filename
			b = new byte[Packet.MAX_SIZE];
			
			//add the first packet that contains the file name
			p = new Packet(3,msg.getID(),count,f.getName().getBytes("UTF-8"));
			msg.addPacket(p);
			count--;
			
			while(br.read(b) != -1){
				p = new Packet(3,msg.getID(),count,b);
				msg.addPacket(p);
				count--;
				b = new byte[Packet.MAX_SIZE];
			}
			br.close();
			return msg;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e){
			//File read error
			e.printStackTrace();
		} catch ( Exception e){
			e.printStackTrace();
		}
		return null;
	}
	public static void write(Message msg){
		try{
			System.out.println("printing: "+msg.getPacket(0).toString());
			String filename = msg.getPacket(0).getText();
			File f= new File(filename);
			if(!f.exists()) {
				f.createNewFile();
			}
			
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));
			for(int i=1;i<msg.getSize();i++){
				out.write(msg.getPacket(i).getContent());
			}
			out.close();	//Close the output stream
		}catch (IOException e){//Catch exception if any
			e.printStackTrace();
		}
	}

}
