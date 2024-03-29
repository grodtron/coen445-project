package coen445.project.common.udp;

import java.net.InetSocketAddress;
import java.util.Collection;

public class DeregDeniedMessage extends UdpMessage {

	public static enum Reason {
		NONE,
		NOT_REGISTERED,
		ADDRESS_MISMATCH,
		NOT_REGISTERED_AT_ADDRESS,
		SELLING_ITEMS,
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
	
	public DeregDeniedMessage(UdpContext context, byte[] rawdata, InetSocketAddress address) {
		super(context, rawdata, address);
		requestNumber = rawdata[1];
		reason        = Reason.get(rawdata[2]);
		
		System.out.println("Created DeregDenied message: " + requestNumber + " " + reason);
	}
	
	public DeregDeniedMessage(DeregisterMessage msg, Reason reason){
		this(
				msg.context,
				new byte[]{
					(byte)UdpMessage.OpCodes.DEREG_DENIED.ordinal(),
					(byte)msg.getRequestNumber(), 
					(byte)reason.ordinal()},
				msg.address);
	}

	@Override
	public Collection<? extends UdpMessage> onReceive() {
		return context.process(this);
	}

}
