package com.stock.user.models.database;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity(name = "user_asset_info")
@Data
public class UserStockInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false)
	private String username;

	@Column(nullable = false)
	private String stockName;

	@Column(nullable = false)
	private Integer quantity;

}
