package com.ccz.appinall.library.util.queue;

public abstract class QReader{
	public int cmd;
	public String data;
	
	protected int id;
	protected String popKey;
	
	public QReader(int id, String popKey) {
		this.id = id;
		this.popKey = popKey;
	}
	
	//public String getKey() {	return popKey;	   }
	
	public abstract long push(int cmd, String data); 
	public abstract boolean pop();
}
