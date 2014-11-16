package coen445.project.common.tcp;

import java.util.Collection;

public class UnknownMessage extends TcpMessage {

	public UnknownMessage(TcpContext context, byte [] data){
		super(context, data, TcpMessage.Mode.RECEIVED);
	}
	
	@Override
	public Collection<? extends TcpMessage> onReceive() {
		return context.process(this);
	}

}
