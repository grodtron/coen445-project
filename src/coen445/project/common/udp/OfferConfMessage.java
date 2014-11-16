package coen445.project.common.udp;

import java.util.Collection;

public class OfferConfMessage extends UdpMessage {

	
	
	public OfferConfMessage(OfferMessage msg, int itemId) {
		super(
			msg.context,
			null,
			msg.address);
		
		byte [] description = msg.getDescription().getBytes();
		
		data = new byte[1 + 1 + 2 + description.length + 1 + 2];
		
		data[0] = (byte)UdpMessage.OpCodes.OFFER_CONF.ordinal();
		data[1] = (byte)msg.getRequestNumber();
		data[2] = (byte)(itemId >> 8);
		data[3] = (byte)itemId;
		data[4] = (byte)description.length;
		System.arraycopy(description, 0, data, 5, description.length);
		data[5 + description.length]     = (byte)(msg.getMinimum()>>8);
		data[5 + 1 + description.length] = (byte) msg.getMinimum();
		
		System.out.println("created new OfferConf");
		
	}

	@Override
	public Collection<? extends UdpMessage> onReceive() {
		return context.process(this);
	}

}
