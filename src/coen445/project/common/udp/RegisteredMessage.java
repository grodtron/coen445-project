package coen445.project.common.udp;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;

public class RegisteredMessage extends UdpMessage {

	private int requestNumber;
	private String name;
	private SocketAddress registeredAddress;
		
	public RegisteredMessage(UdpContext context, byte[] rawdata, InetSocketAddress address) {
		super(context, rawdata, address);
		
		// Get the request number
		requestNumber = (int)rawdata[1];
		
		// Get the name. Strings are prefixed with their length, as this is
		// the most convenient method for Java.
		int nameBegin   = 3;
		int nameLength  = rawdata[2];
		name            = new String(rawdata, nameBegin, nameLength);
		
		// Retrieve the IP Address. It is the next 4 bytes after the string
		// ends
		int ipAddrBegin = nameBegin + nameLength;
		int ipAddrEnd   = ipAddrBegin + 4;
		InetAddress ipAddress;
		try {
			ipAddress   = Inet4Address.getByAddress(Arrays.copyOfRange(rawdata, ipAddrBegin, ipAddrEnd));
		} catch (UnknownHostException e) {
			ipAddress   = null;
			System.err.println("Couldn't get IP Address: " + e);
		}
		
		int portBegin  = ipAddrEnd;
		int portEnd    = ipAddrEnd + 1;
		int port       = ((0xff & rawdata[portBegin]) << 8) | (0xff & rawdata[portEnd]);
				
		registeredAddress = new InetSocketAddress(ipAddress, port);
		
		System.out.println("Created Registered message: " + this);
	}
	
	public RegisteredMessage(RegisterMessage msg){
		this(msg.context, Arrays.copyOf(msg.getData(), msg.getData().length), msg.getAddress());
		data[0] = (byte)UdpMessage.OpCodes.REGISTERED.ordinal();
	}

	@Override
	public Collection<? extends UdpMessage> onReceive() {
		return context.process(this);
	}
	
	@Override
	public String toString(){
		return requestNumber + " " + name + " " + registeredAddress;
	}
	
}
