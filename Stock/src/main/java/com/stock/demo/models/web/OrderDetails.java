package com.stock.demo.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OrderDetails {
	
	@JsonProperty("user_name")
	private String userName;
	
	@JsonProperty("stock_name")
	private String stockName;
	
	
	private int quantity;
	
	
}