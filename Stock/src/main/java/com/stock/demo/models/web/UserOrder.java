package com.stock.demo.models.web;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class UserOrder {

	private String stockName;
	private int quantity;
	private double executedPrice;
	private String orderType;
	private Timestamp timestamp;
	
}
