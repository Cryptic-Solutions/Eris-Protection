package me.spec;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import de.taimos.totp.TOTPData;
import me.spec.crypt.ClientCrypt;
import me.spec.crypt.CryptUtils;
import me.spec.network.SerDes;
import me.spec.network.client.C42069AuthenticationPacket;

public class ClientThread extends Thread {

	private Socket socket;
	private boolean admin;
	private PrintWriter pw;
	
	public ClientThread(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			String line = br.readLine();
			while (line != null) {
				try {
					SerDes serializer = new SerDes(null, Arrays.asList(C42069AuthenticationPacket.class, C42069AuthenticationPacket.class));
					C42069AuthenticationPacket packet = (C42069AuthenticationPacket) serializer.readObject(new DataInputStream(new ByteArrayInputStream(line.getBytes())));
					
					if (packet.ADMIN) {
						if (pw == null) {
							pw = new PrintWriter(this.socket.getOutputStream(), true);
						}
						if (packet.ACTION == null || packet.ACTION.equals("")) {
							this.adminLogin(packet);
						} else {
							this.adminPacket(packet);
						}
					} else {
						whiteList(packet);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					this.socket.close();
				}
				
				try {
					line = br.readLine();
				} catch (SocketException e) {
					br.close();
					this.socket.close();
					return;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void adminPacket(C42069AuthenticationPacket packet) throws IOException, NoSuchMethodException, SecurityException {
		if (!this.admin) {
			this.socket.close();
			return;
		}
		switch (packet.ACTION) {
			case "ADD":
				Main.updateUserCount();
				Main.userCount++;
				File toCreate = new File(Main.directory, Main.userCount + "");
				toCreate.createNewFile();
				PrintWriter pw1 = new PrintWriter(toCreate);
				SerDes serializer = new SerDes(null, Arrays.asList(UserData.class, UserData.class));

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				DataOutputStream output = new DataOutputStream(baos);
				
				UserData data = new UserData();
				data.NAME = packet.NAME;
				serializer.writeObject(data, output);
				output.flush();
				pw1.println(new String(baos.toByteArray()));
				pw1.close();
				pw.println("Added UID=" + toCreate.getName() + ", NAME=" + data.NAME);
				Main.updateUserCount();
				break;
			case "REMOVE":
				File toDelete = new File(Main.directory, packet.UID + "");
				if (toDelete.exists()) {
					if (toDelete.delete()) {
						pw.println("Removed " + packet.UID);
					} else {
						pw.println("Couldn't delete " + packet.UID);
					}
				} else {
					pw.println(packet.UID + " doesn't exist.");
				}
				Main.updateUserCount();
			case "RESETHWID":
				File toUpdate = new File(Main.directory, packet.UID + "");
				if (!toUpdate.exists()) {
					return;
				}
				SerDes serializer2 = new SerDes(null, Arrays.asList(UserData.class, UserData.class));
				UserData object = (UserData) serializer2.readObject(new DataInputStream(new ByteArrayInputStream(Files.readAllLines(Paths.get(toUpdate.getPath())).get(0).getBytes())));
				object.HWID = "";
				
				PrintWriter pw2 = new PrintWriter(toUpdate);
				SerDes serializer21 = new SerDes(null, Arrays.asList(UserData.class, UserData.class));

				ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
				DataOutputStream output2 = new DataOutputStream(baos2);
				
				serializer21.writeObject(object, output2);
				output2.flush();
				pw2.println(new String(baos2.toByteArray()));
				pw2.close();
				pw.println("Reset UID=" + packet.UID + " HWID");
				break;
			case "PASSWORD": 
				File toUpdate2 = new File(Main.directory, packet.UID + "");
				if (!toUpdate2.exists()) {
					return;
				}
				SerDes serializer3 = new SerDes(null, Arrays.asList(UserData.class, UserData.class));
				UserData object2 = (UserData) serializer3.readObject(new DataInputStream(new ByteArrayInputStream(Files.readAllLines(Paths.get(toUpdate2.getPath())).get(0).getBytes())));
				object2.PASSWORD = "";
				
				PrintWriter pw3 = new PrintWriter(toUpdate2);
				SerDes serializer4 = new SerDes(null, Arrays.asList(UserData.class, UserData.class));

				ByteArrayOutputStream baos3 = new ByteArrayOutputStream();
				DataOutputStream output3 = new DataOutputStream(baos3);
				
				serializer4.writeObject(object2, output3);
				output3.flush();
				pw3.println(new String(baos3.toByteArray()));
				pw3.close();
				pw.println("Reset UID=" + packet.UID + " Password");
				break;
			default:
				break;
		}
	}
	
	private void adminLogin(C42069AuthenticationPacket login) throws IOException {
		admin = false;
		File toRead = new File(Main.directory, login.UID + "");
		
		if (!toRead.exists()) {
			this.socket.close();
		} else {
			List<String> lines = Files.readAllLines(Paths.get(toRead.getPath()));
			try {
				SerDes serializer2 = new SerDes(null, Arrays.asList(UserData.class, UserData.class));
				UserData object = (UserData) serializer2.readObject(new DataInputStream(new ByteArrayInputStream(lines.get(0).getBytes())));
				
				if (!object.IsAdmin) {
					this.socket.close();
					System.out.println(login.UID + " - " + object.NAME + " | Tried to login as a admin.");
					return;
				}
				boolean update = false;
				if (object.HWID.equals("")) {
					object.HWID = CryptUtils.hash(login.HWID);
					update = true;
				} else if (!object.HWID.equals(CryptUtils.hash(login.HWID))) {
					this.socket.close();
					System.out.println(login.UID + " - " + object.NAME + " | Failed Admin HWID check.");
					return;
				}
				
				if (!object.PASSWORD.equals(CryptUtils.hash(login.PASSWORD))) {
					this.socket.close();
					return;
				}
				
				if (!login.AUTH_CODE.equals(TOTPData.getTOTPCode(object.AUTH_KEY))) {
					this.socket.close();
					return;
				}
				
				if (!object.ips.contains(this.socket.getInetAddress().getHostAddress())) {
					object.ips.add(this.socket.getInetAddress().getHostAddress());
					update = true;
				}
				
				if (update) {
					PrintWriter pw = new PrintWriter(toRead);
					SerDes serializer = new SerDes(null, Arrays.asList(UserData.class, UserData.class));

					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					DataOutputStream output = new DataOutputStream(baos);
					
					serializer.writeObject(object, output);
					output.flush();
					pw.println(new String(baos.toByteArray()));
					pw.close();
				}
				
				PrintWriter pw = new PrintWriter(this.socket.getOutputStream(), true);
				pw.println("Logged in.");
				
				System.out.println(login.UID + " - " + object.NAME + " | Admin successfully logged in.");
				admin = true;
			} catch (Exception e) {
				e.printStackTrace();
				this.socket.close();
				System.out.println(login.UID + " - admin has failed to logged in.");
			}
		}
	}
	
	private void whiteList(C42069AuthenticationPacket info) {
		try {
			File toRead = new File(Main.directory, info.UID + "");
			
			if (!toRead.exists()) {
				this.socket.close();
			} else {
				List<String> lines = Files.readAllLines(Paths.get(toRead.getPath()));
				try {
					SerDes serializer2 = new SerDes(null, Arrays.asList(UserData.class, UserData.class));
					UserData object = (UserData) serializer2.readObject(new DataInputStream(new ByteArrayInputStream(lines.get(0).getBytes())));
					
					boolean update = false;
					
					if (object.HWID == null || object.HWID.equals("")) {
						update = true;
						object.HWID = CryptUtils.hash(info.HWID);
					} else if (!object.HWID.equals(CryptUtils.hash(info.HWID))) {
						this.socket.close();
						System.out.println(info.UID + " - " + object.NAME + " | Failed HWID check.");
						return;
					}
					
					if (object.PASSWORD == null || object.PASSWORD.equals("")) {
						update = true;
						object.PASSWORD = CryptUtils.hash(info.PASSWORD);
					} else if (!object.PASSWORD.equals(CryptUtils.hash(info.PASSWORD))) {
						this.socket.close();
						return;
					}
					
					if (!object.ips.contains(this.socket.getInetAddress().getHostAddress())) {
						object.ips.add(this.socket.getInetAddress().getHostAddress());
						update = true;
					}
					
					if (update) {
						PrintWriter pw = new PrintWriter(toRead);
						SerDes serializer = new SerDes(null, Arrays.asList(UserData.class, UserData.class));

						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						DataOutputStream output = new DataOutputStream(baos);
						
						serializer.writeObject(object, output);
						output.flush();
						pw.println(new String(baos.toByteArray()));
						pw.close();
					}
					
					PrintWriter pw = new PrintWriter(this.socket.getOutputStream(), true);
					pw.println(ClientCrypt.encrypt("Seb prot is best prot", object.NAME));
					pw.println(ClientCrypt.encrypt("Seb prot", info.PASSWORD));
					
					System.out.println(info.UID + " - " + object.NAME + " | Successfully logged in.");
				} catch (Exception e) {
					e.printStackTrace();
					this.socket.close();
					System.out.println(info.UID + " - has failed to logged in.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
