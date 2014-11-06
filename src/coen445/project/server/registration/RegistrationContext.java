package coen445.project.server.registration;

import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import coen445.project.common.registration.IRegistrationContext;
import coen445.project.common.registration.RegisterMessage;
import coen445.project.common.registration.RegisteredMessage;
import coen445.project.common.registration.UnregisteredMessage;

public class RegistrationContext extends IRegistrationContext {

	private Map<String, InetAddress> registrations;
	
	private DatagramSocket socket;
	
	public RegistrationContext(File persistedFile){
		registrations = new HashMap<String, InetAddress>();
	}
	
	@Override
	public void process(RegisterMessage msg){
		String name = msg.getName();
		
		if(registrations.containsKey(name)){
			return new UnregisteredMessage(msg, UnregisteredMessage.Reason.DUPLICATE_NAME);
		}else{
			registrations.put(name, msg.getIpAddress());
			return new RegisteredMessage(msg);
		}
	}
	
}
