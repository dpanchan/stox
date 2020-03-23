package com.stock.user.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UserProfile {

	@JsonProperty("first_name")
	private String firstName;

	@JsonProperty("last_name")
	private String lastName;

	private String username;

	private double balance;

}