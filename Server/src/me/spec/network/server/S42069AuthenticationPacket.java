package me.spec.network.server;

import java.io.Serializable;

public class S42069AuthenticationPacket implements Serializable {
	public int UID;
	public boolean ADMIN;
	public String AUTH_CODE;
	public String PASSWORD;
	public String HWID;
	public String NAME;
	public String ACTION;
	
}
