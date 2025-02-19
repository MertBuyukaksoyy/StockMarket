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
import java.util.ArrayList;
import java.util.List;

@Service
public class StockService {
    @Autowired
    private StockRepo stockRepo;
    @Autowired
    private StockHistoryRepo stockHistoryRepo;
    @Autowired
    private SseService sseService;

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void fetchStocks(){
        try {
            Document doc = Jsoup.connect("https://bigpara.hurriyet.com.tr/borsa/canli-borsa/")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36")
                    .timeout(90000)
                    .get();

            Elements stockContainers = doc.select("div.tableBox div.tBody.ui-unsortable ul.live-stock-item");
            List<Stock> stockToUpdate = new ArrayList<>();
            List<StockHistory> stockHistories = new ArrayList<>();

            for (Element container : stockContainers) {
                String stockP = container.select("a:nth-of-type(2)").attr("href");
                String stockData = stockP.replace("/borsa/hisse-fiyatlari","");
                String[] parts = stockData.split("-");
                String stockSymbol = parts[0].replace("/","");
                String stockName = String.join("-", java.util.Arrays.copyOfRange(parts,1, parts.length));
                stockName = stockName.replace("detay/", "");
                stockName = stockName.replace("-","");
                stockName = stockName.trim();
                String stockPriceStr = container.select("li[id^=h_td_fiyat_id_]").text();
                BigDecimal stockPrice = new BigDecimal(stockPriceStr.replace(".", "").replace(",", "."));

                Stock existingStock = stockRepo.findByStockSymbol(stockSymbol);
                if(existingStock != null){
                    existingStock.setCurrentPrice(stockPrice);
                    existingStock.setStockName(stockName);
                    stockToUpdate.add(existingStock);

                    StockHistory history = new StockHistory();
                    history.setStock(existingStock);
                    history.setPrice(stockPrice);
                    history.setTimeStamp(LocalDateTime.now());
                    stockHistories.add(history);

                    sseService.sendEvent("stockUpdate", existingStock);
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

            if (!stockToUpdate.isEmpty()){
                for (Stock stock : stockToUpdate) {
                    stockRepo.bulkUpdateStockPrice(stock.getStockSymbol(), stock.getCurrentPrice(), stock.getStockName());
                }
            }
            if (!stockHistories.isEmpty()) {
                stockHistoryRepo.saveAll(stockHistories); // Toplu kaydetme
            }
        }

        catch (IOException e){
            e.printStackTrace();
        }
        }


    public List<Stock> getAllStocks() {
        return stockRepo.findAll();
    }
    public Stock findById(int id){
        return stockRepo.findById(id).orElseThrow(() -> new RuntimeException("Stock cannot be found: " + id));
    }

    public Stock save(Stock stock) {
        return stockRepo.save(stock);
    }

    public void updateStockQuantity(Stock stock, int newQuantity) {
        stock.setStockQuantity(stock.getStockQuantity() + newQuantity);
        stockRepo.save(stock);
    }
}
