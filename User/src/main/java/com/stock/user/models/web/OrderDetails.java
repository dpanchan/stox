package com.stock.user.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OrderDetails {
	@JsonProperty("user_name")
	private String username;
	
	@JsonProperty("stock_name")
	private String stock;
	
	private int quantity;
	
	@JsonProperty("order_type")
	private String orderType;
	
}
