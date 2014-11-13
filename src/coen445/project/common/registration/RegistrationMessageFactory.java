package coen445.project.common.registration;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;

import coen445.project.common.registration.IRegistrationMessage.OpCodes;

public class RegistrationMessageFactory {

	private final IRegistrationContext context;
	
	public RegistrationMessageFactory(IRegistrationContext context){
		this.context = context;
	}
	
	public IRegistrationMessage createMessage(DatagramPacket packet){
		
		byte [] data              = packet.getData();
		InetSocketAddress address = (InetSocketAddress) packet.getSocketAddress();
		
		switch( OpCodes.get(packet.getData()[0]) ){
		case DEREGISTER:
			return new DeregisterMessage(context, data, address);
		case DEREG_CONF:
			return new DeregConfMessage(context, data, address);
		case DEREG_DENIED:
			return new DeregDeniedMessage(context, data, address);
		case REGISTER:
			return new RegisterMessage(context, data, address);
		case REGISTERED:
			return new RegisteredMessage(context, data, address);
		case UNREGISTER:
			return new UnregisteredMessage(context, data, address);
		default: case UNKNOWN:
			return new UnknownMessage(context);
		}
	}
	
}
