package coen445.project.common.tcp;

import java.util.Collection;


public abstract class TcpMessage {

	public static enum OpCodes {
		BID,
		HIGHEST,
		WIN,		// TODO
		BID_OVER,	// TODO
		SOLDTO,		// TODO
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
	
	public static enum Mode {
		UNICAST_RESPONSE,
		BROADCAST,
		UNICAST_ORIGINATOR,
		RECEIVED,
		UNKNOWN;
		
		public static Mode get(byte b){
			OpCodes[] values = OpCodes.values();
			if(b < values.length){
				return Mode.values()[b];
			}else{
				return UNKNOWN;
			}
		}
	}

	
	/**
	 * Indicates what should be done with this message - reply directly, broadcast to all connected clients,
	 * or send to the originator of this item
	 */
	private final Mode mode;
	
	protected byte [] data;
	
	protected TcpContext context;
	
	public TcpMessage(TcpContext context, byte [] data, Mode mode){
		this.data = data;
		this.mode = mode;
		this.context = context;
	}

	public Mode getMode() {
		return mode;
	}

	public byte[] getData() {
		return data;
	}

	public TcpContext getContext() {
		return context;
	}
	
	public abstract Collection<? extends TcpMessage> onReceive();

}
