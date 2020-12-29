package me.spec;

import java.io.IOException;
import java.net.ServerSocket;

public class Server extends Thread {

	private static ServerSocket ss;

	@Override
	public void run() {
		try {
			ss = new ServerSocket(59145);
			
			while (true) {
				try {
					new ClientThread(ss.accept()).start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
