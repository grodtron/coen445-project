package coen445.project.common.registration;

public class DeregDeniedMessage extends IRegistrationMessage {

	public static enum Reason {
		NONE,
		UNKNOWN;
		
		public static Reason get(byte code){
			Reason[] values = Reason.values();
			if(code < values.length){
				return values[code];
			}else{
				return UNKNOWN;
			}
		}
	}
	
	int requestNumber;
	Reason reason;
	
	public DeregDeniedMessage(IRegistrationContext context, byte[] rawdata) {
		super(context, rawdata);
		requestNumber = rawdata[1];
		reason        = Reason.get(rawdata[2]);
		
		System.out.println("Created DeregDenied message: " + requestNumber + " " + reason);
	}

	@Override
	public IRegistrationMessage onReceive() {
		return context.process(this);
	}

}
