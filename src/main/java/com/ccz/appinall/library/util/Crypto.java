package com.ccz.appinall.library.util;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {
	public static class AES256Cipher {
		 
		 private static volatile AES256Cipher s_pThis;
		 public static AES256Cipher getInst() {	 return s_pThis = (s_pThis==null ? new AES256Cipher() : s_pThis);		 }
		 public static void freeInst()	{ 	s_pThis = null;	}
		 
		 private AES256Cipher(){		     
		 }
		 
		 String KEY = "KOREAN_GEO_GRAPHICS_ON_KBS2_1001";
		 String IV  = "201710010128_MON";
		 
		 public String enc(String str) {
			 return this.enc(KEY, str);
		 }
		 
		 public String enc(String key, String str) {
			 try{
			     byte[] keyData = key.getBytes();
			 
				 SecretKey secureKey = new SecretKeySpec(keyData, "AES");
				 
				 Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
				 c.init(Cipher.ENCRYPT_MODE, secureKey, new IvParameterSpec(IV.getBytes()));
				 
				 byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
				 String enStr = Base62.encode(encrypted);
				 
				 return enStr;
			 }catch(Exception e) {
				 e.printStackTrace();				 
				 return str;
			 }
		 }
		 
		 public String dec(String str) {
			 return dec(KEY, str);
		 }
		 public String dec(String key, String str) {
			 try{
				  byte[] byteStr = Base62.decode(str);
				 
				  byte[] keyData = key.getBytes();
				  SecretKey secureKey = new SecretKeySpec(keyData, "AES");
				  Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
				  c.init(Cipher.DECRYPT_MODE, secureKey, new IvParameterSpec(IV.getBytes()));				  
				  byte[] deStr = c.doFinal(byteStr);
				  return new String(deStr,"UTF-8");
			 }catch(Exception e) {
				 e.printStackTrace();
				 return "";
			 }
		 }
	}
	
	public static class RC4Cipher {
		 private static volatile RC4Cipher s_pThis;
		 
		 public static RC4Cipher getInst(){
		     if(s_pThis==null)
                s_pThis=new RC4Cipher();
		     return s_pThis;
		 }
		 public static void freeInst() {
			 s_pThis = null;
		 }
		 
		 private String key = "TECHX";
		 SecretKeySpec rc4Key;
		 
		 private RC4Cipher(){
			try{					
		    	byte [] byKey = key.getBytes("ASCII");	
		    	rc4Key = new SecretKeySpec(byKey, "RC4");
			}catch(Exception e) {
				e.printStackTrace();
			}
		 }
		 
		 public byte[] enc(String plain) {
			 try{
				 Cipher rc4Enc = Cipher.getInstance("RC4");
			     rc4Enc.init(Cipher.ENCRYPT_MODE, rc4Key);
				 return  rc4Enc.doFinal(plain.getBytes("UTF-8"));
			 }catch(Exception e) {
				 e.printStackTrace();
			 }
			 return null;
		 }
		 
		 public String dec(byte[] cipher) {
			 try{
				Cipher rc4Dec = Cipher.getInstance("RC4");
			    rc4Dec.init(Cipher.DECRYPT_MODE, rc4Key);
			    byte [] plain = rc4Dec.doFinal(cipher);	
			    return new String(plain, "UTF-8");
			 }catch(Exception e) {
				 e.printStackTrace();
			 }
			 return null;
		 }	
	}
}

