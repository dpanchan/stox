package com.stock.client.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderDetails {

	@JsonProperty("user_name")
	private String username;
	
	@JsonProperty("stock_name")
	private String stockName;
	
	@JsonProperty("order_type")
	private String orderType;
	
	private int quantity;
	
	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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