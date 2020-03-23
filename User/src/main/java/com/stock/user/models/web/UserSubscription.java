package com.stock.user.models.web;

import lombok.Data;

@Data
public class UserSubscription {

	private String stockname;
	private Double minPrice;
	private Double maxPrice;
	private String username;
	
}
