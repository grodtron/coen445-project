package coen445.project.server.registration;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import coen445.project.common.tcp.TcpMessage;

public class Registrar {

	public static Registrar instance;
	
	static {
		instance = new Registrar();
	}
	
	
	private Map<String, InetSocketAddress> registrationsByName;
	private Map<InetSocketAddress, String> registrationsByAddress;
	private Map<String, Socket> ConnectedClientSockets;
	
	private Registrar(){
		registrationsByName    = new HashMap<String, InetSocketAddress>();
		registrationsByAddress = new HashMap<InetSocketAddress, String>();
		
		ConnectedClientSockets = new HashMap<String, Socket>();
	}
	
	public boolean isRegistered(String name){
		return registrationsByName.containsKey(name);
	}

	public InetSocketAddress getAddress(String name){
		return registrationsByName.get(name);
	}
	
	public void remove(String name){
		InetSocketAddress addr = registrationsByName.get(name);
		if(addr != null){
			registrationsByName.remove(name);
			registrationsByAddress.remove(addr);
			
			Socket sock = ConnectedClientSockets.get(name);
			ConnectedClientSockets.remove(name);
			try {
				sock.close();
			} catch (IOException e) {
				System.err.println("Problem closing client socket: " + e);;
			}
		}
	}
	
	public boolean register(String name, InetSocketAddress addr){
		try {
			Socket socket = new Socket(addr.getAddress(), addr.getPort());
			ConnectedClientSockets.put(name, socket);
		} catch (IOException e) {
			System.err.println("WARNING: Couldn't connect to client, not registering: " + e);
			return false;
		}
		
		registrationsByName.put(name, addr);
		registrationsByAddress.put(addr, name);
		
		return true;
	}
	
	public Set<String> users(){
		return registrationsByName.keySet();
	}

	public String getUser(SocketAddress addr) {
		if(addr instanceof InetSocketAddress){
			return registrationsByAddress.get(addr);
		}else{
			return null;			
		}
	}

	public void informAll(TcpMessage msg) {
		byte [] bytes = msg.getData();
		for(String client : ConnectedClientSockets.keySet()){
			Socket clientSocket = ConnectedClientSockets.get(client);
			try{
				OutputStream stream = clientSocket.getOutputStream();
				stream.write(bytes);
				stream.flush();
			}catch(IOException e){
				remove(client);
			}
		}
		
	}
	
}
