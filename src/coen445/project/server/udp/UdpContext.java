package coen445.project.server.udp;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import coen445.project.common.udp.DeregConfMessage;
import coen445.project.common.udp.DeregDeniedMessage;
import coen445.project.common.udp.DeregisterMessage;
import coen445.project.common.udp.IUdpContext;
import coen445.project.common.udp.IUdpMessage;
import coen445.project.common.udp.NewItemMessage;
import coen445.project.common.udp.OfferConfMessage;
import coen445.project.common.udp.OfferMessage;
import coen445.project.common.udp.RegisterMessage;
import coen445.project.common.udp.RegisteredMessage;
import coen445.project.common.udp.UnregisteredMessage;
import coen445.project.server.inventory.Inventory;

public class UdpContext extends IUdpContext {

	private Map<String, InetSocketAddress> registrations;
	
	private final Inventory inventory;
	
	public UdpContext(File persistedFile){
		registrations = new HashMap<String, InetSocketAddress>();
		inventory     = new Inventory();
	}
	
	@Override
	public Collection<? extends IUdpMessage> process(RegisterMessage msg){
		String name = msg.getName();
		
		if(registrations.containsKey(name)){
			return Collections.singleton(new UnregisteredMessage(msg, UnregisteredMessage.Reason.DUPLICATE_NAME));
		}else
		if( ! msg.getAddress().equals(msg.getAssertedAddress())){
			return Collections.singleton(new UnregisteredMessage(msg, UnregisteredMessage.Reason.ADDRESS_MISMATCH));
		}else{
			registrations.put(name, msg.getAddress());
			return Collections.singleton(new RegisteredMessage(msg));
		}
	}
	
	@Override
	public Collection<? extends IUdpMessage> process(DeregisterMessage msg){
		String name = msg.getName();
		
		SocketAddress registeredAddress = registrations.get(name);
		
		if(registeredAddress == null){
			return Collections.singleton(new DeregDeniedMessage(msg, DeregDeniedMessage.Reason.NOT_REGISTERED));
		}else
		if( ! msg.getAssertedIpAddress().equals(msg.getAddress().getAddress()) )
		{
			return Collections.singleton(new DeregDeniedMessage(msg, DeregDeniedMessage.Reason.ADDRESS_MISMATCH));
		}else
		if( ! registeredAddress.equals(msg.getAddress()) ){
			return Collections.singleton(new DeregDeniedMessage(msg, DeregDeniedMessage.Reason.NOT_REGISTERED_AT_ADDRESS));
		}else{
			registrations.remove(name);
			return Collections.singleton(new DeregConfMessage(msg));
		}
	}
	
	@Override
	public Collection<IUdpMessage> process(OfferMessage msg){
		
		String name                         = msg.getName();
		InetAddress assertedIp              = msg.getAssertedIpAddress();
		InetSocketAddress receivedAddress   = msg.getAddress();
		InetSocketAddress registeredAddress = registrations.get(name);
		
		if( registeredAddress == null ){
			System.out.println("Registered address is NULL");
			return null;
		}else
		if( ! registeredAddress.equals(receivedAddress) ){
			System.out.println("Registered address != received address");
			return null;
		}else
		if( ! registeredAddress.getAddress().equals(assertedIp) ){
			System.out.println("Registered address != assertedIp");
			return null;
		}else{
			int itemId = inventory.addItem(msg.getDescription(), msg.getMinimum());
			if(itemId >= 0){
				List<IUdpMessage> messages = new ArrayList<IUdpMessage>();
				// Confirm the item to the user posting it
				messages.add(new OfferConfMessage(msg, itemId));
				
				// Advertise it to all other registered users
				for(String user : registrations.keySet()){
					if(! user.equals(name) ){
						messages.add(new NewItemMessage(msg, itemId, registrations.get(user)));
					}
				}
				
				return messages;
			}
		}
		
		return null;
	}
}
