package coen445.project.common.registration;

import java.net.InetSocketAddress;

public abstract class IRegistrationMessage {

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
	
	protected IRegistrationContext context;
	
	protected byte [] data;
	
	protected final InetSocketAddress address;
	
	public IRegistrationMessage(IRegistrationContext context, byte [] data, InetSocketAddress address){
		this.context = context;
		this.data    = data;
		this.address = address;
	}
	
	public abstract IRegistrationMessage onReceive();
	
	public byte [] getData(){
		return data;
	}
	
	public InetSocketAddress getAddress(){
		return address;
	}
	
}
