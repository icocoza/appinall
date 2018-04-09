package com.ccz.appinall.library.type.inf;

@FunctionalInterface
public interface ICommandFunction<A, B, C> {
	public B doAction(A a, B b, C c) ;
}
