package com.stock.demo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stock.demo.models.db.StockHistory;

public interface StockHistoryDAO extends JpaRepository<StockHistory, Long> {
	List<StockHistory> findByStockName(String stockName);
}
