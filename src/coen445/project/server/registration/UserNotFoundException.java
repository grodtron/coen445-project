package coen445.project.server.registration;

import java.net.SocketAddress;

public class UserNotFoundException extends Exception {

	public UserNotFoundException(SocketAddress address) {
		super("User with address " + address + " not registered");
	}

	private static final long serialVersionUID = 1L;

}
