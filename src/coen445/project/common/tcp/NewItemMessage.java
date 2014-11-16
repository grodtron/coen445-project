package coen445.project.common.tcp;

import java.net.InetSocketAddress;
import java.util.Collection;

import coen445.project.common.udp.OfferMessage;
import coen445.project.common.udp.UdpMessage;

public class NewItemMessage extends TcpMessage {

	public NewItemMessage(OfferMessage msg, int itemId, InetSocketAddress address) {
		super(null, new byte[0], TcpMessage.Mode.BROADCAST);
		byte [] description = msg.getDescription().getBytes();
		int minimum         = msg.getMinimum();
		int port = itemId;
		
		data = new byte[1 + 2 + description.length + 1 + 2 + 2];
		
		data[0] = (byte)UdpMessage.OpCodes.NEW_ITEM.ordinal();
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

	public NewItemMessage(int itemId, String description, int minimum) {
		super(null, new byte[0], TcpMessage.Mode.BROADCAST);
		byte [] descriptionBytes = description.getBytes();
		int port = itemId;
		
		data = new byte[1 + 2 + descriptionBytes.length + 1 + 2 + 2];
		
		data[0] = (byte)UdpMessage.OpCodes.NEW_ITEM.ordinal();
		data[1] = (byte) (itemId >> 8);
		data[2] = (byte)  itemId;
		data[3] = (byte) descriptionBytes.length;
		System.arraycopy(descriptionBytes, 0, data, 4, descriptionBytes.length);
		data[4 + descriptionBytes.length] = (byte) (minimum >> 8);
		data[4 + 1 + descriptionBytes.length] = (byte) minimum;
		data[4 + 2 + descriptionBytes.length] = (byte) (port >> 8);
		data[4 + 3 + descriptionBytes.length] = (byte) port;
		
		System.out.println("Created new NewItemMessage");	}

	@Override
	public Collection<? extends TcpMessage> onReceive() {
		return null; // TODO context.process(this);
	}

}
