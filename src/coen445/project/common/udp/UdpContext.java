package coen445.project.common.udp;

import java.util.Collection;

public abstract class UdpContext {

	public Collection<? extends UdpMessage> process(DeregConfMessage msg){
		System.out.println("Ignoring unexpected DeregConfMessage");
		return null;
	}
	
	public Collection<? extends UdpMessage> process(DeregDeniedMessage msg){
		System.out.println("Ignoring unexpected DeregDeniedMessage");
		return null;
	}
	
	public Collection<? extends UdpMessage> process(DeregisterMessage msg){
		System.out.println("Ignoring unexpected DeregisterMessage");
		return null;
	}
	
	public Collection<? extends UdpMessage> process(RegisteredMessage msg){
		System.out.println("Ignoring unexpected RegisteredMessage");
		return null;
	}
	
	public Collection<? extends UdpMessage> process(RegisterMessage msg){
		System.out.println("Ignoring unexpected RegisterMessage");
		return null;
	}
	
	public Collection<? extends UdpMessage> process(UnregisteredMessage msg){
		System.out.println("Ignoring unexpected UnregisteredMessage");
		return null;
	}

	public Collection<? extends UdpMessage> process(UnknownMessage msg){
		System.out.println("Ignoring unexpected UnknownMessage");
		return null;
	}
	
	public Collection<? extends UdpMessage> process(OfferMessage msg){
		System.out.println("Ignoring unexpected OfferMessage");
		return null;
	}
	
	public Collection<? extends UdpMessage> process(OfferConfMessage msg){
		System.out.println("Ignoring unexpected OfferConfMessage");
		return null;
	}

}
