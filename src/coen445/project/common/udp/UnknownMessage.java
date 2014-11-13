package coen445.project.common.udp;

import java.util.Collection;


public class UnknownMessage extends IUdpMessage {

	public UnknownMessage(IUdpContext context) {
		super(context, null, null);
		System.out.println("Created new UNKNOWN message");
	}

	@Override
	public Collection<? extends IUdpMessage> onReceive() {
		return context.process(this);
	}
	
}
