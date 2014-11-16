package coen445.project.common.tcp;

import java.net.InetSocketAddress;
import java.util.Collection;

public class SoldtoMessage extends TcpMessage {

	public SoldtoMessage(int itemNumber, String sellerName, InetSocketAddress sellerAddr, int amount){
		super(null, null, TcpMessage.Mode.UNICAST_ORIGINATOR);
		
		byte [] sellerNameBytes = sellerName.getBytes();
		
		data = new byte[ 1 + 2 + 1 + sellerNameBytes.length + 4 + 2 + 2 ];
		
		data[0] = (byte) TcpMessage.OpCodes.WIN.ordinal();
		data[1] = (byte) (0xff & (itemNumber>>8));
		data[2] = (byte) (0xff & itemNumber);
		
		data[3] = (byte)sellerNameBytes.length;
		System.arraycopy(sellerNameBytes, 0, data, 4, sellerNameBytes.length);
		
		byte[] ipAddress = sellerAddr.getAddress().getAddress();
		int port         = sellerAddr.getPort();
		
		data[0 + 4 + sellerNameBytes.length] = ipAddress[0];
		data[1 + 4 + sellerNameBytes.length] = ipAddress[1];
		data[2 + 4 + sellerNameBytes.length] = ipAddress[2];
		data[3 + 4 + sellerNameBytes.length] = ipAddress[3];
		
		data[4 + 4 + sellerNameBytes.length] = (byte) (0xff & (port >> 8));
		data[5 + 4 + sellerNameBytes.length] = (byte) (0xff & port);
		
		data[6 + 4 + sellerNameBytes.length] = (byte) (0xff & (amount >> 8));
		data[7 + 4 + sellerNameBytes.length] = (byte) (0xff & amount);
		
 	}
	
	@Override
	public Collection<? extends TcpMessage> onReceive() {
		return context.process(this);
	}
}
