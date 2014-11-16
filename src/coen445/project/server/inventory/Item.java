package coen445.project.server.inventory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

import coen445.project.common.tcp.BidOverMessage;
import coen445.project.common.tcp.NewItemMessage;
import coen445.project.common.tcp.SoldtoMessage;
import coen445.project.common.tcp.TcpMessage;
import coen445.project.common.tcp.WinMessage;
import coen445.project.server.registration.Registrar;
import coen445.project.server.registration.UserNotFoundException;


public class Item implements Runnable{

	private final String description;
	
	private String highBidder;
	private int currentBid;
	
	private final String sellingUser;

	private final ServerSocket socket;
	
	private Collection<BiddingSession> biddingSessions;
	
	public Item(String description, String sellingUser, int startingBid) throws IOException{
		this.description = description;
		this.socket      = new ServerSocket(0);
		this.currentBid  = startingBid - 1; // because the starting bid itself is also acceptable
		this.highBidder  = null;
		this.sellingUser = sellingUser;
		
		biddingSessions = new ArrayList<BiddingSession>();
	}

	@Override
	public void run() {
		
		new Timer().schedule(new TimerTask() {
			@Override public void run() {
				System.out.println("Removing Item with port #" + socket.getLocalPort());
				
				synchronized(Item.this){
					for (BiddingSession biddingSession : Item.this.biddingSessions) {
						if(highBidder == null){
							Registrar.instance.informAll(new NewItemMessage(socket.getLocalPort(), description, currentBid));
						}else{
							if(biddingSession.getUser().equals(highBidder)){
								biddingSession.addToOutbox(new WinMessage(socket.getLocalPort(), sellingUser, Registrar.instance.getAddress(sellingUser), currentBid));
							}else{
								biddingSession.addToOutbox(new BidOverMessage(socket.getLocalPort(), currentBid));
							}
							
							if(biddingSession.getUser().equals(sellingUser)){
								Registrar.instance.informUser(sellingUser, new SoldtoMessage(socket.getLocalPort(), highBidder, Registrar.instance.getAddress(sellingUser), currentBid));
							}
							
							biddingSession.close();
						}
					}
					
					if(highBidder == null){
						try {
							Item.this.socket.close();
							biddingSessions = null;
						} catch (IOException e) {
							System.err.println("Couldn't close Item server socket: " + e);
						}
					}
				}
			}
		}, 2 * 60 * 1000);
		
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
				if(biddingSessions != null){
					BiddingSession biddingSession;
					try {
						biddingSession = new BiddingSession(sock, this);
					} catch (UserNotFoundException e) {
						System.err.println("WARNING: couldn't find user: " + e);
						break;
					}

					biddingSessions.add(biddingSession);
					new Thread(biddingSession).start();
				}else{
					break;
				}
			}
		}
	}

	public int getId() {
		return socket.getLocalPort();
	}

	public boolean bid(String user, int amount) {
		synchronized(this){
			if(amount > currentBid){
				currentBid = amount;
				highBidder = user;
				return true;
			}else{
				return false;
			}
		}
	}

	public void broadcast(TcpMessage msg) {
		for(BiddingSession biddingSession : biddingSessions){
			biddingSession.addToOutbox(msg);
		}
		
	}
}
