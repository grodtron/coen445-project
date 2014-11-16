package coen445.project.common.tcp;

import java.util.Collection;

public class BidOverMessage extends TcpMessage {

	public BidOverMessage(){
		super(null, null, null);
	}
	
	@Override
	public Collection<? extends TcpMessage> onReceive() {
		return context.process(this);
	}

}
