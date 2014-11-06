package coen445.project.server;

import coen445.project.server.registration.RegistrationServer;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Thread(new RegistrationServer(12358)).start();

	}

}
