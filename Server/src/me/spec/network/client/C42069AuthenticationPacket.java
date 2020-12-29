package me.spec.network.client;

import java.io.Serializable;

public class C42069AuthenticationPacket implements Serializable {

	private static final long serialVersionUID = 1337;
	public int UID;
	public boolean ADMIN;
	public String AUTH_CODE;
	public String PASSWORD;
	public String HWID;
	public String NAME;
	public String ACTION;
	
}
