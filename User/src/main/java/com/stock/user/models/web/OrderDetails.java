package com.stock.user.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderDetails {
	@JsonProperty("user_name")
	private String username;
	
	@JsonProperty("stock_name")
	private String stock;
	
	private int quantity;
	
	@JsonProperty("order_type")
	private String orderType;
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getStock() {
		return stock;
	}

	public void setStock(String stock) {
		this.stock = stock;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

}
