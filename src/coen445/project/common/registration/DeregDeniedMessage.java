package coen445.project.common.registration;

import java.net.InetSocketAddress;

public class DeregDeniedMessage extends IRegistrationMessage {

	public static enum Reason {
		NONE,
		NOT_REGISTERED,
		ADDRESS_MISMATCH,
		NOT_REGISTERED_AT_ADDRESS,
		UNKNOWN;
		
		public static Reason get(byte code){
			Reason[] values = Reason.values();
			if(code < values.length){
				return values[code];
			}else{
				return UNKNOWN;
			}
		}
	}
	
	int requestNumber;
	Reason reason;
	
	public DeregDeniedMessage(IRegistrationContext context, byte[] rawdata, InetSocketAddress address) {
		super(context, rawdata, address);
		requestNumber = rawdata[1];
		reason        = Reason.get(rawdata[2]);
		
		System.out.println("Created DeregDenied message: " + requestNumber + " " + reason);
	}
	
	public DeregDeniedMessage(DeregisterMessage msg, Reason reason){
		this(
				msg.context,
				new byte[]{
					(byte)IRegistrationMessage.OpCodes.DEREG_DENIED.ordinal(),
					(byte)msg.getRequestNumber(), 
					(byte)reason.ordinal()},
				msg.address);
	}

	@Override
	public IRegistrationMessage onReceive() {
		return context.process(this);
	}

}
