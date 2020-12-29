package me.spec;

import java.io.Serializable;

public class WhitelistInfo implements Serializable {
 
	private static final long serialVersionUID = 1337;
	public int UID;
	public String HASHED_PASSWORD;
	public String HASHED_HWID;
}
