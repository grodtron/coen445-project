package coen445.project.server.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Collection;

import coen445.project.common.udp.IUdpContext;
import coen445.project.common.udp.IUdpMessage;
import coen445.project.common.udp.RegistrationMessageFactory;

public class UdpServer implements Runnable {

	private DatagramSocket socket;

	private IUdpContext context;
	private RegistrationMessageFactory factory;
	
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
	
	public UdpServer(int port){
		socket = createSocket(port);
		context = new UdpContext(null/* TODO */);
		factory = new RegistrationMessageFactory(context);
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
				IUdpMessage message  = factory.createMessage(receivedPacket);
				Collection<? extends IUdpMessage> responses = message.onReceive();
				
				if(responses != null){
					for (IUdpMessage response : responses) {
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
