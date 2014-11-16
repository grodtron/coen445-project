package coen445.project.common.udp;

import java.net.InetSocketAddress;
import java.util.Collection;

public abstract class UdpMessage {

	public static enum OpCodes {
		DEREG_CONF,
		DEREG_DENIED,
		DEREGISTER,
		REGISTERED,
		REGISTER,
		UNREGISTER,
		OFFER,
		OFFER_CONF,
		NEW_ITEM,
		UNKNOWN;
		
		public static OpCodes get(byte b){
			OpCodes[] values = OpCodes.values();
			if(b < values.length){
				return OpCodes.values()[b];
			}else{
				return UNKNOWN;
			}
		}
	}
	
	protected UdpContext context;
	
	protected byte [] data;
	
	protected final InetSocketAddress address;
	
	public UdpMessage(UdpContext context, byte [] data, InetSocketAddress address){
		this.context = context;
		this.data    = data;
		this.address = address;
	}
	
	public abstract Collection<? extends UdpMessage> onReceive();
	
	public byte [] getData(){
		return data;
	}
	
	public InetSocketAddress getAddress(){
		return address;
	}
	
}
