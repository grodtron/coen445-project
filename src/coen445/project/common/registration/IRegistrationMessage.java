package coen445.project.common.registration;

public abstract class IRegistrationMessage {

	public static enum OpCodes {
		DEREG_CONF,
		DEREG_DENIED,
		DEREGISTER,
		REGISTERED,
		REGISTER,
		UNREGISTER,
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
	
	public IRegistrationMessage(IRegistrationContext context, byte [] data){
		this.context = context;
		this.data = data;
	}
	
	public abstract IRegistrationMessage onReceive();
	
	public byte [] getData(){
		return data;
	}
	
}
