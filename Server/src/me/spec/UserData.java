package me.spec;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserData implements Serializable {
 
	private static final long serialVersionUID = 42069;
	public String HWID = "";
	public String PASSWORD = "";
	public String NAME = "";
	public boolean IsAdmin = false;
	public String AUTH_KEY = "";
	public List<String> ips = new ArrayList<String>();
}
