package com.example.stockmarket;

import com.example.stockmarket.services.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.stockmarket")
@EnableScheduling
public class StockmarketApplication implements CommandLineRunner {
	@Autowired
	private StockService stockService;

	public static void main(String[] args) {
		SpringApplication.run(StockmarketApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		stockService.fetchStocks();
	}
}
