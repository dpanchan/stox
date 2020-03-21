package com.stock.user.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stock.user.models.database.UserStockInfo;

public interface AssetDAO extends JpaRepository<UserStockInfo, Long> {

	List<UserStockInfo> findByUsername(String username);

}
