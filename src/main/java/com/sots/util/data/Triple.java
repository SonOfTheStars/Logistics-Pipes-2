package com.sots.util.data;

public class Triple<A, B, C> {
	
	private A first;
	private B secnd;
	private C third;
	
	public Triple(A first, B secnd, C third) {
		this.first = first;
		this.secnd = secnd;
		this.third = third;
	}

	public A getFirst() {
		return first;
	}

	public B getSecnd() {
		return secnd;
	}

	public C getThird() {
		return third;
	}
	
	
}
