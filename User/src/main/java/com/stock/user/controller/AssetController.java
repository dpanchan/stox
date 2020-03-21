package com.stock.user.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.stock.user.dao.AssetDAO;
import com.stock.user.models.database.UserStockInfo;
import com.stock.user.models.web.UserStock;

@RestController
public class AssetController {

	@Autowired
	private AssetDAO assetdao;

	@GetMapping("/stocks/user/{username}")
	public List<UserStock> getStocksByUsername(@PathVariable("username") String user) {
		List<UserStockInfo> assets = assetdao.findByUsername(user);
		List<UserStock> response = new ArrayList<>();
		for(UserStockInfo asset: assets) {
			response.add(createResponse(asset));
		}
		return response;
	}
	
	private UserStock createResponse(UserStockInfo info) {
		UserStock ustock = new UserStock();
		ustock.setStockName(info.getStockName());
		ustock.setQuantity(info.getQuantity());
		return ustock;
	}

}
