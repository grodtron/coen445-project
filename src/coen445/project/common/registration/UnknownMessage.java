package coen445.project.common.registration;


public class UnknownMessage extends IRegistrationMessage {

	public UnknownMessage(IRegistrationContext context) {
		super(context, null, null);
		System.out.println("Created new UNKNOWN message");
	}

	@Override
	public IRegistrationMessage onReceive() {
		return context.process(this);
	}
	
}
