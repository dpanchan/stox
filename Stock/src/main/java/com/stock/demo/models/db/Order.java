package com.stock.demo.models.db;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity(name = "order_history")
@Data
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long ID;

	@Column(nullable = false)
	private String userName;

	@Column(nullable = false)
	private String stockName;

	@Column(nullable = false)
	private String buyOrSell;

	@Column(nullable = false)
	private int quantity;

	@Column(nullable = false)
	private Timestamp date;

	@Column(nullable = false)
	private BigDecimal currentPrice;



}