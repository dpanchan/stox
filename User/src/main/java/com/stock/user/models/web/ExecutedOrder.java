package com.stock.user.models.web;

import lombok.Data;

@Data
public class ExecutedOrder {

	private String stockName;
	private double executedPrice;
	private int quantity;


}
