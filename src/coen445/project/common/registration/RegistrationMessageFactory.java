package coen445.project.common.registration;

import coen445.project.common.registration.IRegistrationMessage.OpCodes;

public class RegistrationMessageFactory {

	private final IRegistrationContext context;
	
	public RegistrationMessageFactory(IRegistrationContext context){
		this.context = context;
	}
	
	public IRegistrationMessage createMessage(byte [] rawdata){
		
		switch( OpCodes.get(rawdata[0]) ){
		case DEREGISTER:
			return new DeregisterMessage(context, rawdata);
		case DEREG_CONF:
			return new DeregConfMessage(context, rawdata);
		case DEREG_DENIED:
			return new DeregDeniedMessage(context, rawdata);
		case REGISTER:
			return new RegisterMessage(context, rawdata);
		case REGISTERED:
			return new RegisteredMessage(context, rawdata);
		case UNREGISTER:
			return new UnregisteredMessage(context, rawdata);
		default: case UNKNOWN:
			return new UnknownMessage(context);
		}
	}
	
}
