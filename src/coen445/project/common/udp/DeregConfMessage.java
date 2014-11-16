package coen445.project.common.udp;

import java.net.InetSocketAddress;
import java.util.Collection;

public class DeregConfMessage extends UdpMessage {

	private int requestNumber;
	
	public DeregConfMessage(UdpContext context, byte[] rawdata, InetSocketAddress address) {
		super(context, rawdata, address);
		requestNumber = rawdata[1];
		
		System.out.println("Created DeregConf message: " + requestNumber);
	}
	
	public DeregConfMessage(DeregisterMessage msg){
		this(
			msg.context,
			new byte[]{
				(byte)UdpMessage.OpCodes.DEREG_CONF.ordinal(),
				(byte)msg.getRequestNumber() },
			msg.getAddress() );
	}

	@Override
	public Collection<? extends UdpMessage> onReceive() {
		return context.process(this);
	}

}
