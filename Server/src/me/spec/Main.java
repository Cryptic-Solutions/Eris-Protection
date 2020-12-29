package me.spec;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

import me.spec.crypt.CryptUtils;
import me.spec.network.SerDes;

public class Main {

	public static final File directory;
	
	public static int userCount;
	
	static {
		directory = new File("Users");
		if (!directory.exists() && !directory.mkdir()) {
			System.out.println("There was an error creating the users folder.");
			System.exit(-1);
		}
	}
	
	public static void main(String[] args) {
		System.out.println("Starting server...");
		
		updateUserCount();
		
		new Server().start();
		
		System.out.println("Started server. There are " + userCount + " users.");
		System.out.println();
		
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		while (true) {
			String[] line = scanner.nextLine().split(" ");
			if (line[0].equals("add") && line.length > 3) {
				try {
					updateUserCount();
					File toCreate = new File(Main.directory, Main.userCount++ + "");
					toCreate.createNewFile();
					PrintWriter pw1 = new PrintWriter(toCreate);
					SerDes serializer = new SerDes(null, Arrays.asList(UserData.class, UserData.class));

					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					DataOutputStream output = new DataOutputStream(baos);
					
					UserData data = new UserData();
					data.NAME = line[1];
					data.PASSWORD = CryptUtils.hash(line[2]);
					data.AUTH_KEY = line[3];
					data.IsAdmin = true;
					serializer.writeObject(data, output);
					output.flush();
					pw1.println(new String(baos.toByteArray()));
					pw1.close();
					System.out.println("Added admin account, UID=" + toCreate.getName() + ", NAME=" + data.NAME);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (line[0].equals("count")) {
				updateUserCount();
				System.out.println("There is " + userCount + " users.");
			}
		}
	}
	
	public static void updateUserCount() {
		int count = 0;
		for (File file : directory.listFiles()) {
			try {
				int id = Integer.parseInt(file.getName());
				if (id > count) {
					count = id;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		userCount = count;
	}
}
