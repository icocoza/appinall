package com.ccz.appinall.library.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {
	static public byte[] getSha256(String msg) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		return digest.digest(msg.getBytes(StandardCharsets.UTF_8));
	}
	
	static public String getSha256Base62(String msg) {
		try {
			return Base62.encode(getSha256(msg));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
	}

	static public byte[] getSha1(String msg) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA1");
		return digest.digest(msg.getBytes(StandardCharsets.UTF_8));
	}
	
	static public String getSha1Base62(String msg) {
		try {
			return Base62.encode(getSha1(msg));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
	}

}
