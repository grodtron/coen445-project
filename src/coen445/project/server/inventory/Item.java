package coen445.project.server.inventory;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Timer;
import java.util.TimerTask;


public class Item implements Runnable{

	private final String description;
	private final int currentBid;
	
	private final ServerSocket socket;
	
	public Item(String description, int startingBid) throws IOException{
		this.description = description;
		this.socket      = new ServerSocket(0);
		this.currentBid  = startingBid;
	}

	@Override
	public void run() {
		
		new Timer().schedule(new TimerTask() {
			@Override public void run() {
				// TODO kill this
				// Item.this.doSomething()
				System.out.println("Removing Item with port #" + socket.getLocalPort());
			}
		}, 5000);
		
		while(true){
			try {
				// TODO keep track of these somehow
				new Thread(new BiddingSession(socket.accept(), this)).start();
			} catch (IOException e) {
				System.err.println("could create new bidding session: " + e);
			}
		}
	}

	public int getId() {
		return socket.getLocalPort();
	}
}
