package coen445.project.server.inventory;

import java.util.Collection;
import java.util.Collections;

import coen445.project.common.tcp.BidMessage;
import coen445.project.common.tcp.HighestMessage;
import coen445.project.common.tcp.TcpContext;
import coen445.project.common.tcp.TcpMessage;

public class BiddingSessionContext extends TcpContext {

	private final Item item;
	private final String user;
	
	public BiddingSessionContext(Item item, String user){
		this.item = item;
		this.user = user;
	}
	
	@Override
	public Collection<? extends TcpMessage> process(BidMessage msg){
		if( item.bid(user, msg.getAmount()) ){
			item.broadcast(new HighestMessage(msg));
		}
		return Collections.emptySet();
	}
	
}
