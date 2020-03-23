package com.stock.demo;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.stock.demo.dao.StockHistoryDAO;
import com.stock.demo.dao.Stockrepo;
import com.stock.demo.models.db.Stock;
import com.stock.demo.models.db.StockHistory;

@EnableDiscoveryClient
@SpringBootApplication
public class StockApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(StockApplication.class, args);
	}

	@Autowired
	private Stockrepo stockRepo;

	@Autowired
	private StockHistoryDAO stockHistoryRepo;

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder b) {
		return b.build();
	}

	@Override
	public void run(String... args) throws Exception {
		// clean up history
		stockRepo.deleteAll();
		stockHistoryRepo.deleteAll();
		// create initial stock data
		var stocks = loadInitialData();
		// change stock every 10 secs
		while (true) {
			System.out.println(LocalDateTime.now() + " simulating stocks ... ");
			simulateAndUpdate(stocks);
			Thread.sleep(10000);
		}
	}

	public Map<String, Double> loadInitialData() {
		Map<String, Double> stocks = new HashMap<>();

		stocks.put("AMZN", 2200.10);
		stocks.put("FB", 210.60);
		stocks.put("AAPL", 260.09);
		stocks.put("DBX", 23.254);
		stocks.put("IBM", 100.98);
		stocks.put("TSLA", 710.90);
		stocks.put("BABA", 161.986);
		stocks.put("GOOG", 1067.342);

		stocks.forEach((stockName, price) -> {
			var stock = new Stock();
			stock.setName(stockName);
			stock.setPrice(BigDecimal.valueOf(price));
			stock.setAvailable(10000L);
			stockRepo.save(stock);
		});

		return stocks;
	}

	private void simulateAndUpdate(Map<String, Double> stocks) {
		Random r = new Random();
		for (String stockname : stocks.keySet()) {
			double diff = r.nextInt(3) * 0.01 * stocks.get(stockname);
			diff = r.nextBoolean() ? diff : -diff;
			Double newPrice = stocks.get(stockname) + diff;
			stocks.put(stockname, newPrice);

			var stockHistory = new StockHistory();
			stockHistory.setStockName(stockname);
			stockHistory.setGain(diff);
			stockHistory.setCurrentPrice(newPrice);
			stockHistory.setTimestamp(new Timestamp(new Date().getTime()));
			stockHistoryRepo.save(stockHistory);

			var stock = stockRepo.findByName(stockname);
			stock.setPrice(BigDecimal.valueOf(newPrice));
			stockRepo.save(stock);

		}
	}
}
