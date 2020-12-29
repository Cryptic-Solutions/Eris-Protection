package me.spec.crypt;

import java.security.MessageDigest;

public class CryptUtils {
	
    static byte[] salt = {
        (byte) 0xA9, (byte) 0x2B, (byte) 0xC2, (byte) 0x1F,
        (byte) 0x26, (byte) 0x25, (byte) 0xB3, (byte) 0x317
    };
    
    public static String hash(String input) {
		try { 
			MessageDigest md = MessageDigest.getInstance("SHA-256"); 
			md.update(salt);
			byte[] hash = md.digest(input.getBytes("UTF-8"));
			StringBuilder sb = new StringBuilder(2*hash.length);
			for(byte b : hash) { 
				sb.append(String.format("%02x", b&0xff));
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
    }
}
