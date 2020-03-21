package com.stock.demo.models.web;

public class ExecutedOrder {

	private String stockName;
	private double executedPrice;
	private int quantity;

	public double getExecutedPrice() {
		return executedPrice;
	}

	public void setExecutedPrice(double executedPrice) {
		this.executedPrice = executedPrice;
	}

	public String getStockName() {
		return stockName;
	}

	public void setStockName(String stockName) {
		this.stockName = stockName;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

}
