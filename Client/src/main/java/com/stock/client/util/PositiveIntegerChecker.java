package com.stock.client.util;

public class PositiveIntegerChecker implements InputChecker<Integer> {

	public final static Integer QUIT = -1;
	
	private int min;
	private int max;
	
	public PositiveIntegerChecker() {
		this(1, Integer.MAX_VALUE);
	}
	
	public PositiveIntegerChecker(int min, int max) {
		this.min = min;
		this.max = max;
	}

	
	@Override
	public boolean valid(String data) {
		try {
			Integer d = Integer.valueOf(data);
			return d > 0 && d >= min && d <= max;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public Integer convertFromString(String input) {
		return Integer.valueOf(input);
	}

	@Override
	public Integer quit() {
		return QUIT;
	} 

}
