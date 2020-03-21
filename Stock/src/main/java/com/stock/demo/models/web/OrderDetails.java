package com.stock.demo.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderDetails {
	
	@JsonProperty("user_name")
	private String userName;
	
	@JsonProperty("stock_name")
	private String stockName;
	
	
	private int quantity;
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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