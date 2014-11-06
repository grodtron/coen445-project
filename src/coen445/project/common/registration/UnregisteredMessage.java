package coen445.project.common.registration;


public class UnregisteredMessage extends IRegistrationMessage {

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
	
	private int requestNumber;
	private Reason reason;
		
	public UnregisteredMessage(IRegistrationContext context, byte[] rawdata) {
		super(context, rawdata);
		requestNumber = rawdata[1];
		reason        = Reason.get(rawdata[2]);
		
		data = rawdata;
		
		System.out.println("Created Unregister message " + requestNumber + " " + reason);
	}
	
	public UnregisteredMessage(RegisterMessage msg, Reason reason){
		this(msg.context, new byte[ ] { (byte)OpCodes.UNREGISTER.ordinal(), (byte)msg.getRequestNumber(), (byte)reason.ordinal() });
	}

	@Override
	public IRegistrationMessage onReceive() {
		return context.process(this);
	}
}
