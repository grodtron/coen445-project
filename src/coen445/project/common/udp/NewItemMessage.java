package coen445.project.common.udp;

import java.net.InetSocketAddress;
import java.util.Collection;

public class NewItemMessage extends IUdpMessage {

	public NewItemMessage(OfferMessage msg, int itemId, InetSocketAddress address) {
		super(msg.context, new byte[0], address);
		byte [] description = msg.getDescription().getBytes();
		int minimum         = msg.getMinimum();
		int port = itemId;
		
		data = new byte[1 + 2 + description.length + 1 + 2 + 2];
		
		data[0] = (byte)IUdpMessage.OpCodes.NEW_ITEM.ordinal();
		data[1] = (byte) (itemId >> 8);
		data[2] = (byte)  itemId;
		data[3] = (byte) description.length;
		System.arraycopy(description, 0, data, 4, description.length);
		data[4 + description.length] = (byte) (minimum >> 8);
		data[4 + 1 + description.length] = (byte) minimum;
		data[4 + 2 + description.length] = (byte) (port >> 8);
		data[4 + 3 + description.length] = (byte) port;
		
		System.out.println("Created new NewItemMessage");
	}

	@Override
	public Collection<? extends IUdpMessage> onReceive() {
		return null; // TODO context.process(this);
	}

}
