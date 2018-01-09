package com.ccz.appinall.library.util.queue;


public abstract class QWorker {
	public int cmd;
	
	public boolean isQWorker(int cmd) {
		if(this.cmd == cmd)
			return true;
		return false;
	}
	
	public abstract boolean doWork(String value);
}
