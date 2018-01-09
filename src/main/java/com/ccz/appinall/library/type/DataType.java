package com.ccz.appinall.library.type;

public class DataType {
	
	public class Pair<T> {
		final public String key;
		final public T value;
		
		public Pair(String k, T v) {
			this.key = k;
			this.value = v;
		}
		
		public String key() {
			return key;
		}
		public T value() {
			return value;
		}
	}
	
	public class IpPort extends Pair<Integer> {
		public IpPort(String k, Integer v) {
			super(k, v);
		}
		
		public String getIp() { return key; }
		public int getPort() { return value; }
	}
	
}
