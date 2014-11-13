package coen445.project.server.inventory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;


public class Item implements Runnable{

	private final String description;
	private final int currentBid;
	
	private final ServerSocket socket;
	
	private Collection<Thread> biddingThreads;
	
	public Item(String description, int startingBid) throws IOException{
		this.description = description;
		this.socket      = new ServerSocket(0);
		this.currentBid  = startingBid;
		
		biddingThreads = new ArrayList<Thread>();
	}

	@Override
	public void run() {
		
		new Timer().schedule(new TimerTask() {
			@Override public void run() {
				System.out.println("Removing Item with port #" + socket.getLocalPort());
				
				synchronized(Item.this){
					for (Thread biddingThread : Item.this.biddingThreads) {
						biddingThread.interrupt();
					}
					
					try {
						Item.this.socket.close();
						biddingThreads = null;
					} catch (IOException e) {
						System.err.println("Couldn't close Item server socket: " + e);
					}
				}
			}
		}, 1 * 60 * 1000);
		
		while(true){
			Socket sock;
			try {
				sock = socket.accept();
			} catch (SocketException e) {
				System.out.println("Socket closed, we're done");
				break;
			} catch (IOException e) {
				System.err.println("Couldn't accept connection: " + e);
				break;
			}
			
			synchronized(this){
				if(biddingThreads != null){
					// TODO keep track of these somehow
					Thread biddingThread = new Thread(new BiddingSession(sock, this));
					
					biddingThreads.add(biddingThread);
					biddingThread.start();
				}else{
					break;
				}
			}
		}
	}

	public int getId() {
		return socket.getLocalPort();
	}
}
