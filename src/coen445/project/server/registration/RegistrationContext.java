package coen445.project.server.registration;

import java.io.File;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

import coen445.project.common.registration.DeregConfMessage;
import coen445.project.common.registration.DeregDeniedMessage;
import coen445.project.common.registration.DeregisterMessage;
import coen445.project.common.registration.IRegistrationContext;
import coen445.project.common.registration.IRegistrationMessage;
import coen445.project.common.registration.RegisterMessage;
import coen445.project.common.registration.RegisteredMessage;
import coen445.project.common.registration.UnregisteredMessage;

public class RegistrationContext extends IRegistrationContext {

	private Map<String, SocketAddress> registrations;
	
	public RegistrationContext(File persistedFile){
		registrations = new HashMap<String, SocketAddress>();
	}
	
	@Override
	public IRegistrationMessage process(RegisterMessage msg){
		String name = msg.getName();
		
		if(registrations.containsKey(name)){
			return new UnregisteredMessage(msg, UnregisteredMessage.Reason.DUPLICATE_NAME);
		}else
		if( ! msg.getAddress().equals(msg.getAssertedAddress())){
			return new UnregisteredMessage(msg, UnregisteredMessage.Reason.ADDRESS_MISMATCH);
		}else{
			registrations.put(name, msg.getAddress());
			return new RegisteredMessage(msg);
		}
	}
	
	@Override
	public IRegistrationMessage process(DeregisterMessage msg){
		String name = msg.getName();
		
		SocketAddress registeredAddress = registrations.get(name);
		
		if(registeredAddress == null){
			return new DeregDeniedMessage(msg, DeregDeniedMessage.Reason.NOT_REGISTERED);
		}else
		if( ! msg.getAssertedIpAddress().equals(msg.getAddress().getAddress()) )
		{
			return new DeregDeniedMessage(msg, DeregDeniedMessage.Reason.ADDRESS_MISMATCH);
		}else
		if( ! registeredAddress.equals(msg.getAddress()) ){
			return new DeregDeniedMessage(msg, DeregDeniedMessage.Reason.NOT_REGISTERED_AT_ADDRESS);
		}else{
			registrations.remove(name);
			return new DeregConfMessage(msg);
		}
		
		
	}
}
