package coen445.project.common.udp;

import java.net.InetSocketAddress;
import java.util.Collection;


public class UnregisteredMessage extends UdpMessage {

	public static enum Reason {
		NONE,
		DUPLICATE_NAME,
		ADDRESS_MISMATCH,
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
	
	private int requestNumber;
	private Reason reason;
		
	public UnregisteredMessage(UdpContext context, byte[] rawdata, InetSocketAddress address) {
		super(context, rawdata, address);
		requestNumber = rawdata[1];
		reason        = Reason.get(rawdata[2]);
		
		data = rawdata;
		
		System.out.println("Created Unregistered message " + requestNumber + " " + reason);
	}
	
	public UnregisteredMessage(RegisterMessage msg, Reason reason){
		this(
				msg.context,
				new byte[ ] { (byte)OpCodes.UNREGISTER.ordinal(), (byte)msg.getRequestNumber(), (byte)reason.ordinal() },
				msg.getAddress());
	}

	@Override
	public Collection<? extends UdpMessage> onReceive() {
		return context.process(this);
	}
}
