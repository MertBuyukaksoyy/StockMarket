package com.example.stockmarket.services;

import com.example.stockmarket.dao.StockRepo;
import com.example.stockmarket.entity.Stock;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
public class StockService {
    @Autowired
    private StockRepo stockRepo;

  //  @Scheduled(fixedRate = 5000)
    public void fetchStocks(){
        try {
            Document doc = Jsoup.connect("https://bigpara.hurriyet.com.tr/borsa/canli-borsa/")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36")
                    .timeout(5000)
                    .get();

            Elements stockContainers = doc.select("div.tableBox div.tBody.ui-unsortable ul.live-stock-item");
            for (Element container : stockContainers) {
                String stockP = container.select("a:nth-of-type(2)").attr("href");
                String stockData = stockP.replace("/borsa/hisse-fiyatlari","");
                String[] parts = stockData.split("-");
                String stockSymbol = parts[0];
                String stockName = String.join("-", java.util.Arrays.copyOfRange(parts,1, parts.length));
              //  stockName = stockName.substring(stockName.lastIndexOf("/") + 1, stockName.lastIndexOf("-detay"));
                String stockPriceStr = container.select("li[id^=h_td_fiyat_id_]").text();
                BigDecimal stockPrice = new BigDecimal(stockPriceStr.replace(".", "").replace(",", "."));

                Stock existingStock = stockRepo.findByStockSymbol(stockSymbol);
                if(existingStock != null){
                    existingStock.setCurrentPrice(stockPrice);
                    existingStock.setStockName(stockName);
                    stockRepo.save(existingStock);
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
    public Stock findById(int id){
        return stockRepo.findById(id).orElseThrow(() -> new RuntimeException("Hisse bulunamadı: " + id));
    }

    public Stock save(Stock stock) {
        return stockRepo.save(stock);
    }
}
