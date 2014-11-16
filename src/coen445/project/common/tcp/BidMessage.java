package coen445.project.common.tcp;

import java.util.Collection;

public class BidMessage extends TcpMessage {

	private final int requestNumber;
	private final int itemNumber;
	private final int amount;
	
	public BidMessage(byte [] rawdata, TcpContext context){
		super(context, rawdata, TcpMessage.Mode.RECEIVED);
		// rawdata[0] is the opcode. We're gonna trust the caller (since it's us)
		requestNumber = ((0xff & rawdata[1])<<8) | (0xff & rawdata[2]);
		itemNumber    = ((0xff & rawdata[3])<<8) | (0xff & rawdata[4]);
		amount        = ((0xff & rawdata[5])<<8) | (0xff & rawdata[6]);
	}
	
	@Override
	public Collection<? extends TcpMessage> onReceive() {
		return context.process(this);
	}

	public int getRequestNumber() {
		return requestNumber;
	}

	public int getItemNumber() {
		return itemNumber;
	}

	public int getAmount() {
		return amount;
	}

}
