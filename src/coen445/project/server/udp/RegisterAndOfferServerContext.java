package coen445.project.server.udp;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import coen445.project.common.tcp.NewItemMessage;
import coen445.project.common.udp.DeregConfMessage;
import coen445.project.common.udp.DeregDeniedMessage;
import coen445.project.common.udp.DeregisterMessage;
import coen445.project.common.udp.OfferConfMessage;
import coen445.project.common.udp.OfferMessage;
import coen445.project.common.udp.RegisterMessage;
import coen445.project.common.udp.RegisteredMessage;
import coen445.project.common.udp.UdpContext;
import coen445.project.common.udp.UdpMessage;
import coen445.project.common.udp.UnregisteredMessage;
import coen445.project.server.inventory.Inventory;
import coen445.project.server.inventory.Item;
import coen445.project.server.registration.Registrar;

public class RegisterAndOfferServerContext extends UdpContext {
	
	private final Inventory inventory;
	
	public RegisterAndOfferServerContext(File persistedFile){
		inventory     = new Inventory();
	}
	
	@Override
	public Collection<? extends UdpMessage> process(RegisterMessage msg){
		String name = msg.getName();
		
		if(Registrar.instance.isRegistered(name)){
			return Collections.singleton(new UnregisteredMessage(msg, UnregisteredMessage.Reason.DUPLICATE_NAME));
		}else
		if( ! msg.getAddress().equals(msg.getAssertedAddress())){
			return Collections.singleton(new UnregisteredMessage(msg, UnregisteredMessage.Reason.ADDRESS_MISMATCH));
		}else{
			if(Registrar.instance.register(name, msg.getAddress())){
				Set<Item> items = inventory.getPendingItems();
								
				for(Item item : items){
					Registrar.instance.informUser(name, new NewItemMessage(item.getId(), item.getDescription(), item.getHighBid()));
				}				
				
				return Collections.singleton(new RegisteredMessage(msg));
			}else{
				return Collections.emptySet();
			}
		}
	}
	
	@Override
	public Collection<? extends UdpMessage> process(DeregisterMessage msg){
		String name = msg.getName();
		
		SocketAddress registeredAddress = Registrar.instance.getAddress(name);
		
		if(registeredAddress == null){
			return Collections.singleton(new DeregDeniedMessage(msg, DeregDeniedMessage.Reason.NOT_REGISTERED));
		}else
		if( ! msg.getAssertedIpAddress().equals(msg.getAddress().getAddress()) )
		{
			return Collections.singleton(new DeregDeniedMessage(msg, DeregDeniedMessage.Reason.ADDRESS_MISMATCH));
		}else
		if( ! registeredAddress.equals(msg.getAddress()) ){
			return Collections.singleton(new DeregDeniedMessage(msg, DeregDeniedMessage.Reason.NOT_REGISTERED_AT_ADDRESS));
		}else
		if(! inventory.canDeregister(name)){
			return Collections.singleton(new DeregDeniedMessage(msg, DeregDeniedMessage.Reason.SELLING_ITEMS));
		}else{
			Registrar.instance.remove(name);
			return Collections.singleton(new DeregConfMessage(msg));
		}
	}
	
	@Override
	public Collection<UdpMessage> process(OfferMessage msg){
		
		String name                         = msg.getName();
		InetAddress assertedIp              = msg.getAssertedIpAddress();
		InetSocketAddress receivedAddress   = msg.getAddress();
		InetSocketAddress registeredAddress = Registrar.instance.getAddress(name);
		
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
			int itemId = inventory.addItem(msg.getDescription(), msg.getName(), msg.getMinimum());
			if(itemId >= 0){
				List<UdpMessage> messages = new ArrayList<UdpMessage>();
				// Confirm the item to the user posting it
				messages.add(new OfferConfMessage(msg, itemId));
				
				// Advertise it to all other registered users
				Registrar.instance.informAll(new NewItemMessage(msg, itemId, msg.getAddress()));
				
				return messages;
			}
		}
		
		return null;
	}
}
