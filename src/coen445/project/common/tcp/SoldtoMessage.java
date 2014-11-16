package coen445.project.common.tcp;

import java.util.Collection;

public class SoldtoMessage extends TcpMessage {

	public SoldtoMessage(){
		super(null, null, null);
	}
	
	@Override
	public Collection<? extends TcpMessage> onReceive() {
		return context.process(this);
	}
}
