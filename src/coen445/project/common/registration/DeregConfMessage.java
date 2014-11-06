package coen445.project.common.registration;

public class DeregConfMessage extends IRegistrationMessage {

	private int requestNumber;
	
	public DeregConfMessage(IRegistrationContext context, byte[] rawdata) {
		super(context, rawdata);
		requestNumber = rawdata[1];
		
		System.out.println("Created DeregConf message: " + requestNumber);
	}

	@Override
	public IRegistrationMessage onReceive() {
		return context.process(this);
	}

}
