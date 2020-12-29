package me.spec.pannel;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

import javax.swing.JOptionPane;

import me.spec.network.SerDes;
import me.spec.network.client.C42069AuthenticationPacket;

public class SocketListener {

	private int UID;
	private String password;
	private String auth_code;
	
	private Socket s;
	private BufferedReader br;
	private PrintWriter pw;
	
	public SocketListener(int UID, String password, String auth_code) {
		this.UID = UID;
		this.password = password;
		this.auth_code = auth_code;
	}
	
	public void sendPacket(C42069AuthenticationPacket packet) throws IOException, NoSuchMethodException, SecurityException {
		SerDes serializer = new SerDes(null, Arrays.asList(C42069AuthenticationPacket.class, C42069AuthenticationPacket.class));

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream output = new DataOutputStream(baos);
		
		serializer.writeObject(packet, output);
		output.flush();
		pw.println(new String(baos.toByteArray()));
	}
	
	public void run() {
		try {
			s = new Socket("127.0.0.1", 59145);
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			pw = new PrintWriter(s.getOutputStream(), true);
			
			C42069AuthenticationPacket info = new C42069AuthenticationPacket();
			info.UID = UID;
			info.PASSWORD = password;
			info.AUTH_CODE = auth_code;
			info.ADMIN = true;
			info.HWID = HWID.getHWID();
			SerDes serializer = new SerDes(null, Arrays.asList(C42069AuthenticationPacket.class, C42069AuthenticationPacket.class));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream output = new DataOutputStream(baos);
			serializer.writeObject(info, output);
			output.flush();
			pw.println(new String(baos.toByteArray()));
			
			if (s.isClosed()) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						JOptionPane.showMessageDialog(null, "Failed to connect to the server", "Admin Panel", JOptionPane.ERROR_MESSAGE);
					}
					
				}).start();
			} else {
				
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							String line = br.readLine();
							if (line == null) {
								new Thread(new Runnable() {

									@Override
									public void run() {
										JOptionPane.showMessageDialog(null, "Failed to connect to the server", "Admin Panel", JOptionPane.ERROR_MESSAGE);
									}
									
								}).start();
							}
							while (line != null) {
								String line2 = line;
								new Thread(new Runnable() {

									@Override
									public void run() {
										JOptionPane.showMessageDialog(null, line2, "Admin Panel", JOptionPane.INFORMATION_MESSAGE);
									}
									
								}).start();
								try {
									line = br.readLine();
								} catch (Exception e) {
									JOptionPane.showMessageDialog(null, "Lost connection to server", "Admin Panel", JOptionPane.ERROR_MESSAGE);
									e.printStackTrace();
								}
							}
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					
				}).start();
			}
		} catch (Exception e) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, "Failed to connect to the server", "Admin Panel", JOptionPane.ERROR_MESSAGE);
				}
				
			}).start();
			e.printStackTrace();
		}
	}
}
