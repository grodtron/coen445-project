package coen445.project.server.inventory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import coen445.project.common.tcp.TcpMessage;
import coen445.project.common.tcp.TcpMessageFactory;
import coen445.project.server.registration.Registrar;
import coen445.project.server.registration.UserNotFoundException;

public class BiddingSession implements Runnable {

	private final Socket socket;
	private boolean closed;
	private BiddingSessionContext context;
	private TcpMessageFactory factory;
	private final String user;
	
	LinkedBlockingQueue<TcpMessage> outbox;
	
	public BiddingSession(Socket socket, Item item) throws UserNotFoundException{				
		SocketAddress address = socket.getRemoteSocketAddress();
		this.user = Registrar.instance.getUser(address);
		
		if(this.user == null){
			throw new UserNotFoundException(address);
		}
		
		this.context = new BiddingSessionContext(item, user);
		this.factory = new TcpMessageFactory(context);
		this.socket  = socket;
		this.closed  = false;
		
		this.outbox = new LinkedBlockingQueue<>();
	}
	
	public void close(){
		synchronized(this){
			try {
				socket.close();
			} catch (IOException e) {
				System.err.println("Socket could not be closed: " + e);
			}
			closed = true;
		}
	}
	
	@Override
	public void run() {
		final InputStream   input;
		final OutputStream  output;
		try {
			output = socket.getOutputStream();
			input  = socket.getInputStream();
		} catch (IOException e) {
			System.err.println("Couldn't get input and/or output stream: " + e);
			return;
		}
		
		// Outbox processing thread, just blocks on the queue all the time
		new Thread(new Runnable() {
			@Override
			public void run() {
				while( ! BiddingSession.this.closed){ // maybe need to synchronize on this, I dunno
					// block for a very very long time :P
					try{
						TcpMessage toSend = outbox.poll(Long.MAX_VALUE, TimeUnit.DAYS);
						output.write(toSend.getData());
						output.flush();
					}catch(Exception e){
						System.err.println("can't write: " + e);
						e.printStackTrace();
						return;
					}
							
				}
			}
		}).start();
		
		while(true){
			byte [] buffer = new byte[256];
			int length;
			
			try {
				length = input.read(buffer);
			} catch (IOException e) {
				synchronized(this){
					if(! closed){
						System.err.println("Can't read: " + e);
						return;
					}else{
						System.out.println("Closed, ending");
						return;
					}
				}
			}
			synchronized(this){
				if(closed){
					System.out.println("Closed, ending");
					return;
				}
				if(length > 0){
					System.out.println("received " + length + " bytes");
					
					TcpMessage received = factory.createMessage(buffer);
					
					Collection<? extends TcpMessage> responses =  received.onReceive();
					
					for(TcpMessage response : responses){
						outbox.add(response);
					}
				}else{
					System.out.println("Read 0 or less bytes, it's over");
					break;
				}
			}
			
		}
	}

	public void addToOutbox(TcpMessage msg) {
		outbox.add(msg);
	}

	public String getUser() {
		return this.user;
	}

}
