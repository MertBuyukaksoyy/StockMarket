package com.example.stockmarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.stockmarket")
public class StockmarketApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockmarketApplication.class, args);
	}


}
