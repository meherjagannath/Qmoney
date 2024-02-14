package com.crio.warmup.stock.portfolio;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Callable;
import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;

public class PortfolioCallable implements Callable<AnnualizedReturn> {
  private PortfolioTrade symbol;
  private LocalDate endDate;
  private StockQuotesService service;

    

  @Override
  public AnnualizedReturn call() throws Exception {
        
    List<Candle> collection = getStockQuote(symbol.getSymbol(), symbol.getPurchaseDate(), endDate);
    AnnualizedReturn x = calculateAnnualizedReturns(endDate, symbol, collection.get(0).getOpen(),
            collection.get(collection.size() - 1).getClose());
        
        
    return x;
  }

  public PortfolioCallable(PortfolioTrade symbol, LocalDate endDate, StockQuotesService service) {
    this.symbol = symbol;
    this.endDate = endDate;
    this.service = service;
  }

  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) 
        throws JsonProcessingException, StockQuoteServiceException {
    //  ObjectMapper om = getObjectMapper();
    //  String result = restTemplate.getForObject(buildUri(symbol, from, to), String.class);
    //  List<TiingoCandle> collection = om.readValue
    //  (result, new TypeReference<ArrayList<TiingoCandle>>() {
    //  });
    //  return new ArrayList<Candle>(collection);
    return this.service.getStockQuote(symbol, from, to);
      
  }
    
  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate, 
      PortfolioTrade trade, Double buyPrice,Double sellPrice) {
    Double totalReturn = (sellPrice - buyPrice) / buyPrice;
    long daysBetween = ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate);
    double years = (double) daysBetween / 365;
    Double annualret = Math.pow(1 + totalReturn, 1 / years) - 1;
    return new AnnualizedReturn(trade.getSymbol(), annualret, totalReturn);
    
  }
    
    
}