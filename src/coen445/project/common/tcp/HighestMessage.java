package coen445.project.common.tcp;

import java.util.Collection;

public class HighestMessage extends TcpMessage {

	private final int itemNumber;
	private final int amount;
	
	public HighestMessage(BidMessage msg){
		this(msg.getItemNumber(), msg.getAmount(), msg.getContext());
	}
	
	public HighestMessage(int itemNumber, int amount, TcpContext context){
		super(
			context,
			new byte[]{
				(byte) TcpMessage.OpCodes.HIGHEST.ordinal(),
				(byte) (0xff & (itemNumber >> 8)),
				(byte) (0xff & itemNumber),
				(byte) (0xff & (amount >> 8)),
				(byte) (0xff & amount)
			},
			TcpMessage.Mode.BROADCAST);
		
		this.itemNumber = itemNumber;
		this.amount     = amount;
	}
	
	@Override
	public Collection<? extends TcpMessage> onReceive() {
		return context.process(this);
	}

	public int getItemNumber() {
		return itemNumber;
	}

	public int getAmount() {
		return amount;
	}

}
