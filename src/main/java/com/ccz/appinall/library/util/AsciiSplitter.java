package com.ccz.appinall.library.util;

public class AsciiSplitter {
	public static final String CHUNK = String.valueOf(Character.toChars(27)),
			   FILE = String.valueOf(Character.toChars(28)),
			   GROUP = String.valueOf(Character.toChars(29)),
			   RECORD = String.valueOf(Character.toChars(30)),
			   UNIT = String.valueOf(Character.toChars(31));

	public static final String ETB = String.valueOf(Character.toChars(23)),
				   EM  = String.valueOf(Character.toChars(25));
	
	public static String[] splitChunk(String s) {
	return s.split(CHUNK);
	}
	
	public static String[] splitFile(String s) {
	return s.split(FILE);
	}
	
	public static String[] splitGroup(String s) {
	return s.split(GROUP);
	}
	
	public static String[] splitRecord(String s) {
	return s.split(RECORD);
	}
	
	public static String[] splitUnit(String s) {
	return s.split(UNIT);
	}
	
	public static String[] splitEndOfBlock(String s) {
	return s.split(ETB);
	}
	
	public static String[] splitEndOfMedium(String s) {
	return s.split(EM);
	}
	
	public class ASS extends AsciiSplitter {	/*void*/	}
}
