package coen445.project.common.registration;

import java.net.InetSocketAddress;

public class DeregConfMessage extends IRegistrationMessage {

	private int requestNumber;
	
	public DeregConfMessage(IRegistrationContext context, byte[] rawdata, InetSocketAddress address) {
		super(context, rawdata, address);
		requestNumber = rawdata[1];
		
		System.out.println("Created DeregConf message: " + requestNumber);
	}
	
	public DeregConfMessage(DeregisterMessage msg){
		this(
			msg.context,
			new byte[]{
				(byte)IRegistrationMessage.OpCodes.DEREG_CONF.ordinal(),
				(byte)msg.getRequestNumber() },
			msg.getAddress() );
	}

	@Override
	public IRegistrationMessage onReceive() {
		return context.process(this);
	}

}
