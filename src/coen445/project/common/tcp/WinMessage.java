package coen445.project.common.tcp;

import java.util.Collection;

public class WinMessage extends TcpMessage {

	public WinMessage(){
		super(null, null, null);
	}
	
	@Override
	public Collection<? extends TcpMessage> onReceive() {
		return context.process(this);
	}

}
