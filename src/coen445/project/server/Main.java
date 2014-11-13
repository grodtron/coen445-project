package coen445.project.server;

import coen445.project.server.udp.UdpServer;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Thread(new UdpServer(12358)).start();

	}

}
