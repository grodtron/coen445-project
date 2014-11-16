package coen445.project.common.udp;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;

public class OfferMessage extends UdpMessage {

	private int requestNumber;
	private String name;
	private InetAddress ipAddress;
	private String description;
	private int minimum;
	
	public OfferMessage(UdpContext context, byte[] rawdata, InetSocketAddress address) {
		super(context, rawdata, address);
		
		requestNumber = (int) data[1];
		
		// Get the name. Strings are prefixed with their length, as this is
		// the most convenient method for Java.
		int nameBegin   = 3;
		int nameLength  = rawdata[2];
		name            = new String(rawdata, nameBegin, nameLength);
		
		// Retrieve the IP Address. It is the next 4 bytes after the string
		// ends
		int ipAddrBegin = nameBegin + nameLength;
		int ipAddrEnd   = ipAddrBegin + 4;
		try {
			ipAddress   = Inet4Address.getByAddress(Arrays.copyOfRange(rawdata, ipAddrBegin, ipAddrEnd));
		} catch (UnknownHostException e) {
			ipAddress   = null;
			System.err.println("Couldn't get IP Address: " + e);
		}
		
		int descriptionBegin  = ipAddrEnd + 1;
		int descriptionLength = (int) rawdata[ipAddrEnd];
		description           = new String(rawdata, descriptionBegin, descriptionLength);
		
		int minimumBegin = descriptionBegin + descriptionLength;
		int minimumEnd   = minimumBegin + 1;
		minimum          = ((0xff & rawdata[minimumBegin])<<8) | (0xff & rawdata[minimumEnd]);
		
		System.out.println("Created offer message: " + this);
		
	}

	@Override
	public String toString(){
		return "Offer " + name + " " + ipAddress + " \"" + description + "\" " + minimum;
	}
	
	@Override
	public Collection<? extends UdpMessage> onReceive() {
		return context.process(this);
	}

	public int getRequestNumber() {
		return requestNumber;
	}

	public String getName() {
		return name;
	}

	public InetAddress getAssertedIpAddress() {
		return ipAddress;
	}

	public String getDescription() {
		return description;
	}

	public int getMinimum() {
		return minimum;
	}
}
