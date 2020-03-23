package com.stock.user.models.database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity(name = "subscription")
@Data
public class Subscription {

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private Long ID;

	@Column(nullable = false)
	private String username;

	@Column(nullable = false)
	private String stockname;

	@Column(nullable = false)
	private Double minPrice;

	@Column(nullable = false)
	private Double maxPrice;
	
}
