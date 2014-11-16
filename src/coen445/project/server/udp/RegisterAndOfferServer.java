package coen445.project.server.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Collection;

import coen445.project.common.udp.UdpContext;
import coen445.project.common.udp.UdpMessage;
import coen445.project.common.udp.UdpMessageFactory;

public class RegisterAndOfferServer implements Runnable {

	private DatagramSocket socket;

	private UdpContext context;
	private UdpMessageFactory factory;
	
	private static DatagramSocket createSocket(int port){
		DatagramSocket sock = null;
		
		try {
			sock = new DatagramSocket(port);
			System.out.println("Socket opened on port " + port);
		} catch (SocketException e) {
			System.err.println("Could not open UDP socket for registration server: " + e);
			System.exit(1);
		}
		
		return sock;
	}
	
	public RegisterAndOfferServer(int port){
		socket = createSocket(port);
		context = new RegisterAndOfferServerContext(null/* TODO */);
		factory = new UdpMessageFactory(context);
	}
		
	private DatagramPacket receivePacket(){
		DatagramPacket packet = null;
		
		try {
			packet = new DatagramPacket(new byte[256], 256);
			socket.receive(packet);
			System.out.println("packet received");
		} catch (IOException e) {
			System.err.println("Could not receive UDP packet: " + e);
		}
		
		return packet;
	}
	
	@Override
	public void run() {
	
		System.out.println("Registration Server Started");
		
		while(true){
			DatagramPacket receivedPacket = receivePacket();
			
			if(receivedPacket != null){
				UdpMessage message  = factory.createMessage(receivedPacket);
				Collection<? extends UdpMessage> responses = message.onReceive();
				
				if(responses != null){
					for (UdpMessage response : responses) {
						byte [] data = response.getData();
						
						try {
							DatagramPacket packetToSend = new DatagramPacket(data, data.length, response.getAddress());
							try {
								socket.send(packetToSend);
							} catch (IOException e) {
								System.err.println("Could not send response DatagramPacket: " + e);
							}
						} catch (SocketException e) {
							System.err.println("Could not create DatagramPacket to send: " + e);
						}						
					}
				}
			}
		}
		
	}
	
}
