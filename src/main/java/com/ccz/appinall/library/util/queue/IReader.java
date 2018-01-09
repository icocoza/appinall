package com.ccz.appinall.library.util.queue;

public interface IReader {
	public long push(int cmd, String data);
	public boolean pop();
}
