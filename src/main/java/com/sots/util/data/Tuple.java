package com.sots.util.data;

public class Tuple<K, V> {
	
	private K key;
	
	private V val;
	
	public Tuple(K Key, V Val){
		key = Key;
		val = Val;
	}

	public K getKey() {
		return key;
	}

	public V getVal() {
		return val;
	}
	
}
