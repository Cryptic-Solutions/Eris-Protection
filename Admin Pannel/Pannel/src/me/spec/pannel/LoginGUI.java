package me.spec.pannel;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import me.spec.network.client.C42069AuthenticationPacket;

public class LoginGUI extends JFrame { 
	private static final long serialVersionUID = 1337;

	private JPanel contentPane;

	private SocketListener socket;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginGUI frame = new LoginGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	public LoginGUI() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 279, 303);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel login = new JPanel();
		login.setBorder(new EmptyBorder(5, 5, 5, 5));
		login.setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.add("Login", login);
		tabbedPane.setBounds(0, 0, 273, 274);
		contentPane.add(tabbedPane);
		
		JTextArea uid = new JTextArea();
		uid.setBounds(10, 26, 247, 22);
		login.add(uid);
		
		JLabel lblNewLabel = new JLabel("UID");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(10, 11, 247, 18);
		login.add(lblNewLabel);
		
		JTextArea password = new JTextArea();
		password.setBounds(10, 69, 247, 22);
		login.add(password);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setHorizontalAlignment(SwingConstants.CENTER);
		lblPassword.setBounds(10, 54, 247, 18);
		login.add(lblPassword);
		
		JTextArea authcode = new JTextArea();
		authcode.setBounds(10, 112, 247, 22);
		login.add(authcode);
		
		JLabel lblAuthCode = new JLabel("Auth Code");
		lblAuthCode.setHorizontalAlignment(SwingConstants.CENTER);
		lblAuthCode.setBounds(10, 97, 247, 18);
		login.add(lblAuthCode);
		
		JButton Login = new JButton("Login");
		Login.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				socket = new SocketListener(Integer.parseInt(uid.getText()), password.getText(), authcode.getText());
				socket.run();
			}
			
		});
		Login.setBounds(60, 145, 140, 66);
		login.add(Login);
		
		JPanel main = new JPanel();
		tabbedPane.addTab("Main Panel", null, main, null);
		main.setLayout(null);
		
		JTextArea uidMain = new JTextArea();
		uidMain.setBounds(10, 21, 248, 22);
		main.add(uidMain);
		
		JLabel lblNewLabel_1 = new JLabel("UID");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(10, 0, 248, 25);
		main.add(lblNewLabel_1);
		
		JTextArea name = new JTextArea();
		name.setBounds(10, 74, 248, 22);
		main.add(name);
		
		JLabel lblNewLabel_1_1 = new JLabel("Name");
		lblNewLabel_1_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1_1.setBounds(10, 53, 248, 25);
		main.add(lblNewLabel_1_1);
		
		JButton add = new JButton("Add User");
		add.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				C42069AuthenticationPacket packet = new C42069AuthenticationPacket();
				packet.NAME = name.getText();
				packet.ADMIN = true;
				packet.ACTION = "ADD";
				try {
					socket.sendPacket(packet);
				} catch (NoSuchMethodException | SecurityException | IOException e) {
					e.printStackTrace();
				}
			}
			
		});
		add.setBounds(10, 124, 104, 23);
		main.add(add);
		
		JButton btnRemoveUser = new JButton("Remove User");
		btnRemoveUser.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				C42069AuthenticationPacket packet = new C42069AuthenticationPacket();
				packet.UID = Integer.parseInt(uidMain.getText());
				packet.ACTION = "REMOVE";
				packet.ADMIN = true;
				try {
					socket.sendPacket(packet);
				} catch (NoSuchMethodException | SecurityException | IOException e1) {
					e1.printStackTrace();
				}
			}
			
		});
		btnRemoveUser.setBounds(124, 124, 134, 23);
		main.add(btnRemoveUser);
		
		JButton btnResetUser = new JButton("Reset HWID");
		btnResetUser.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				C42069AuthenticationPacket packet = new C42069AuthenticationPacket();
				packet.UID = Integer.parseInt(uidMain.getText());
				packet.ACTION = "RESETHWID";
				packet.ADMIN = true;
				try {
					socket.sendPacket(packet);
				} catch (NoSuchMethodException | SecurityException | IOException e1) {
					e1.printStackTrace();
				}
			}
			
		});
		btnResetUser.setBounds(10, 158, 104, 23);
		main.add(btnResetUser);
		
		JButton btnResetPassword = new JButton("Reset Password");
		btnResetPassword.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				C42069AuthenticationPacket packet = new C42069AuthenticationPacket();
				packet.UID = Integer.parseInt(uidMain.getText());
				packet.ACTION = "PASSWORD";
				packet.ADMIN = true;
				try {
					socket.sendPacket(packet);
				} catch (NoSuchMethodException | SecurityException | IOException e1) {
					e1.printStackTrace();
				}
			}
			
		});
		btnResetPassword.setBounds(124, 158, 134, 23);
		main.add(btnResetPassword);
	}
}
