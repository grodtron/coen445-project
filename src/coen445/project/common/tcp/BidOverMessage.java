package coen445.project.common.tcp;

import java.util.Collection;

public class BidOverMessage extends TcpMessage {

	public BidOverMessage(int itemNumber, int amount){
		super(null, null, TcpMessage.Mode.BROADCAST);
		
		data = new byte[1 + 2 + 2];
		
		data[0] = (byte) TcpMessage.OpCodes.BID_OVER.ordinal();
		
		data[1] = (byte) (0xff & (itemNumber >> 8));
		data[2] = (byte) (0xff & itemNumber);
		
		data[3] = (byte) (0xff & (amount >> 8));
		data[4] = (byte) (0xff & amount);
	}
	
	@Override
	public Collection<? extends TcpMessage> onReceive() {
		return context.process(this);
	}

}
