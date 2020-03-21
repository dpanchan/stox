package com.stock.client.util;

public interface InputChecker<T> {
	boolean valid(String data);
	T convertFromString(String input);
	T quit();
}
