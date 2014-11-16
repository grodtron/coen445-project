package coen445.project.server;

import coen445.project.server.udp.RegisterAndOfferServer;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Thread(new RegisterAndOfferServer(12358)).start();

	}

}
