package coen445.project.common.udp;

import java.util.Collection;


public class UnknownMessage extends UdpMessage {

	public UnknownMessage(UdpContext context) {
		super(context, null, null);
		System.out.println("Created new UNKNOWN message");
	}

	@Override
	public Collection<? extends UdpMessage> onReceive() {
		return context.process(this);
	}
	
}
