package com.stock.user.models.web;

public class Deposit {
	
	private String username;
	public String getusername() {
		return username;
	}
	public void setusername(String username) {
		this.username = username;
	}
	public Double getDeposit() {
		return deposit;
	}
	public void setDeposit(Double deposit) {
		this.deposit = deposit;
	}
	private Double deposit;

}
