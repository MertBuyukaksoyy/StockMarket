package com.example.stockmarket.services;

import com.example.stockmarket.dao.StockHistoryRepo;
import com.example.stockmarket.dao.StockRepo;
import com.example.stockmarket.entity.Stock;
import com.example.stockmarket.entity.StockHistory;
import jakarta.transaction.Transactional;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockService {
    @Autowired
    private StockRepo stockRepo;
    @Autowired
    private StockHistoryRepo stockHistoryRepo;

    @Transactional
    @Scheduled(fixedRate = 90000)
    public void fetchStocks(){
        try {
            Document doc = Jsoup.connect("https://bigpara.hurriyet.com.tr/borsa/canli-borsa/")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36")
                    .timeout(90000)
                    .get();

            Elements stockContainers = doc.select("div.tableBox div.tBody.ui-unsortable ul.live-stock-item");
            for (Element container : stockContainers) {
                String stockP = container.select("a:nth-of-type(2)").attr("href");
                String stockData = stockP.replace("/borsa/hisse-fiyatlari","");
                String[] parts = stockData.split("-");
                String stockSymbol = parts[0];
                String stockName = String.join("-", java.util.Arrays.copyOfRange(parts,1, parts.length));
                String stockPriceStr = container.select("li[id^=h_td_fiyat_id_]").text();
                BigDecimal stockPrice = new BigDecimal(stockPriceStr.replace(".", "").replace(",", "."));

                Stock existingStock = stockRepo.findByStockSymbol(stockSymbol);
                if(existingStock != null){
                    updateStock(existingStock, stockPrice, stockName);
                    saveStockHistory(existingStock);
                }
                else {
                Stock stock = new Stock();
                stock.setStockName(stockName);
                stock.setStockSymbol(stockSymbol);
                stock.setCurrentPrice(stockPrice);
                stock.setStockActive(true);
                stock.setStockQuantity(10000);
                stockRepo.save(stock);
                }

               // System.out.println( "İsim: "  + stockName + "Sembol:" + stockSymbol + ", Fiyat: " + stockPrice);
                }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        }


    public List<Stock> getAllStocks() {
        return stockRepo.findAll();
    }
    public BigDecimal getCurrentStockPrice(String stockSymbol) {
        Stock stock = stockRepo.findByStockSymbol(stockSymbol);
        if (stock != null) {
            return stock.getCurrentPrice();
        }
        throw new RuntimeException("Stock cannot be found: " + stockSymbol);
    }

    public Stock findById(int id){
        return stockRepo.findById(id).orElseThrow(() -> new RuntimeException("Stock cannot be found: " + id));
    }

    public Stock save(Stock stock) {
        return stockRepo.save(stock);
    }
    @Transactional
    private void saveStockHistory(Stock existingStock) {
        StockHistory history = new StockHistory();
        history.setStock(existingStock);
        history.setPrice(existingStock.getCurrentPrice());
        history.setTimeStamp(LocalDateTime.now());
        stockHistoryRepo.save(history);
    }
    @Transactional
    public void updateStock(Stock existingStock, BigDecimal stockPrice, String stockName) {
        existingStock.setCurrentPrice(stockPrice);
        existingStock.setStockName(stockName);
        stockRepo.save(existingStock);
    }

    public void updateStockQuantity(Stock stock, int newQuantity) {
        stock.setStockQuantity(stock.getStockQuantity() + newQuantity);
        stockRepo.save(stock);
    }



}
