package com.stock.demo.models.db;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Entity(name = "stock_history")
@Data
@JsonIgnoreProperties("id")
public class StockHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonProperty("id")
	private Long ID;

	@Column(nullable = false)
	private String stockName;

	@Column(nullable = false)
	private Double currentPrice;

	@Column(nullable = false)
	private Timestamp timestamp;

	@Column(nullable = false)
	private Double gain;

	
}
