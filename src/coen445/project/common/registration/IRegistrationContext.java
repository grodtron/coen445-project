package coen445.project.common.registration;

public abstract class IRegistrationContext {

	public IRegistrationMessage process(DeregConfMessage msg){
		System.out.println("Ignoring unexpected DeregConfMessage");
		return null;
	}
	
	public IRegistrationMessage process(DeregDeniedMessage msg){
		System.out.println("Ignoring unexpected DeregDeniedMessage");
		return null;
	}
	
	public IRegistrationMessage process(DeregisterMessage msg){
		System.out.println("Ignoring unexpected DeregisterMessage");
		return null;
	}
	
	public IRegistrationMessage process(RegisteredMessage msg){
		System.out.println("Ignoring unexpected RegisteredMessage");
		return null;
	}
	
	public IRegistrationMessage process(RegisterMessage msg){
		System.out.println("Ignoring unexpected RegisterMessage");
		return null;
	}
	
	public IRegistrationMessage process(UnregisteredMessage msg){
		System.out.println("Ignoring unexpected UnregisteredMessage");
		return null;
	}

	public IRegistrationMessage process(UnknownMessage msg){
		System.out.println("Ignoring unexpected UnknownMessage");
		return null;
	}
	
}
