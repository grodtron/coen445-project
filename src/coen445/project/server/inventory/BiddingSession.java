package coen445.project.server.inventory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class BiddingSession implements Runnable {

	private final Socket socket;
	private final Item   item;
	
	public BiddingSession(Socket socket, Item item){
		this.socket = socket;
		this.item   = item;
	}
	
	@Override
	public void run() {
		InputStream   input;
		OutputStream  output;
		try {
			output = socket.getOutputStream();
			input  = socket.getInputStream();
		} catch (IOException e) {
			System.err.println("Couldn't get input and/or output stream: " + e);
			return;
		}
		
		while(true){
			byte [] buffer = new byte[256];
			int length;
			
			try {
				length = input.read(buffer);
			} catch (IOException e) {
				System.err.println("Can't read: " + e);
				return;
			}
			
			if(length > 0){
				System.out.println("received \"" + new String(buffer, 0, length) + "\"");
				try {
					output.write(Thread.currentThread().getName().getBytes());
					output.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				System.out.println("Read 0 or less bytes, it's over");
				break;
			}
			
		}
	}

}
