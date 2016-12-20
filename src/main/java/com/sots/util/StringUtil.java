package com.sots.util;

public class StringUtil {
	public static boolean isNullOrEmpty(String a){
		return a==null || a.isEmpty();
	}
	
	public static boolean isNullOrWhitespace(String a){
		return a==null || isWhitespace(a);
	}
	
	private static boolean isWhitespace(String s) {
	    int length = s.length();
	    if (length > 0) {
	        for (int start = 0, middle = length / 2, end = length - 1; start <= middle; start++, end--) {
	            if (s.charAt(start) > ' ' || s.charAt(end) > ' ') {
	                return false;
	            }
	        }
	        return true;
	    }
	    return false;
	}
}
