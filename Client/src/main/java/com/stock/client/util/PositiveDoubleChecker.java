package com.stock.client.util;

public class PositiveDoubleChecker implements InputChecker<Double> {

	public final static Double QUIT = -1.0;
	
	@Override
	public boolean valid(String data) {
		try {
			Double d = Double.valueOf(data);
			return d > 0.0;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public Double convertFromString(String input) {
		return Double.valueOf(input);
	}

	@Override
	public Double quit() {
		return QUIT;
	}

}
