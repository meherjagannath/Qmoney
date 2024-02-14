package com.crio.warmup.stock;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;



public class PortfolioManagerApplication {
  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  //  Task:
  //       - Read the json file provided in the argument[0], The file is available in the classpath.
  //       - Go through all of the trades in the given file,
  //       - Prepare the list of all symbols a portfolio has.
  //       - if "trades.json" has trades like
  //         [{ "symbol": "MSFT"}, { "symbol": "AAPL"}, { "symbol": "GOOGL"}]
  //         Then you should return ["MSFT", "AAPL", "GOOGL"]
  //  Hints:
  //    1. Go through two functions provided - #resolveFileFromResources() and #getObjectMapper
  //       Check if they are of any help to you.
  //    2. Return the list of all symbols in the same order as provided in json.

  //  Note:
  //  1. There can be few unused imports, you will need to fix them to make the build pass.
  //  2. You can use "./gradlew build" to check if your code builds successfully.
  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {

    if (args.length == 0) {

      throw new IllegalArgumentException("Missing JSON file argument.");
    }
    // Initialize an ObjectMapper to read JSON
    ObjectMapper objectMapper = getObjectMapper();
    // Read the JSON file from the provided argument
    File file = resolveFileFromResources(args[0]);
    //Deserialize JSON to POJO
    PortfolioTrade[] tradesFromJson = objectMapper.readValue(file, PortfolioTrade[].class);
   //List<PortfolioTrade> portofolioTrades = new Collection<>();
    //Create a list to store symbols
    List<String> symbolList = new ArrayList<>();
        //Itreate through the trade and extract symbol
    for (PortfolioTrade trade : tradesFromJson) {

      String symbol =trade.getSymbol();
 
      symbolList.add(symbol);
      
    }
    //print symbol to console
    for (String symbol : symbolList) {

      System.out.println("Symbol: " + symbol);

    }

    return symbolList;
 // return Collections.emptyList();
   }

  private static void printJsonObject(Object object) throws IOException {

    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());

    ObjectMapper mapper = new ObjectMapper();

    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(
        Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();

  }

  private static ObjectMapper getObjectMapper() {

    ObjectMapper objectMapper = new ObjectMapper();

    objectMapper.registerModule(new JavaTimeModule());
    
    return objectMapper;

  }
  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  //  Follow the instructions provided in the task documentation and fill up the correct values for
  //  the variables provided. First value is provided for your reference.
  //  A. Put a breakpoint on the first line inside mainReadFile() which says
  //    return Collections.emptyList();
  //  B. Then Debug the test #mainReadFile provided in PortfoliomanagerApplicationTest.java
  //  following the instructions to run the test.
  //  Once you are able to run the test, perform following tasks and record the output as a
  //  String in the function below.
  //  Use this link to see how to evaluate expressions -
  //  https://code.visualstudio.com/docs/editor/debugging#_data-inspection
  //  1. evaluate the value of "args[0]" and set the value
  //     to the variable named valueOfArgument0 (This is implemented for your reference.)
  //  2. In the same window, evaluate the value of expression below and set it
  //  to resultOfResolveFilePathArgs0
  //     expression ==> resolveFileFromResources(args[0])
  //  3. In the same window, evaluate the value of expression below and set it
  //  to toStringOfObjectMapper.
  //  You might see some garbage numbers in the output. Dont worry, its expected.
  //    expression ==> getObjectMapper().toString()
  //  4. Now Go to the debug window and open stack trace. Put the name of the function you see at
  //  second place from top to variable functionNameFromTestFileInStackTrace
  //  5. In the same window, you will see the line number of the function in the stack trace window.
  //  assign the same to lineNumberFromTestFileInStackTrace
  //  Once you are done with above, just run the corresponding test and
  //  make sure its working as expected. use below command to do the same.
  //  ./gradlew test --tests PortfolioManagerApplicationTest.testDebugValues

  public static List<String> debugOutputs() {

     String valueOfArgument0 = "trades.json";

     String resultOfResolveFilePathArgs0 = "/home/crio-user/workspace/meher-jagannath453-ME_QMONEY_V2/qmoney/bin/main/trades.json";
     
     String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@6150c3ec";
    
     String functionNameFromTestFileInStackTrace = "mainReadFile";
    
     String lineNumberFromTestFileInStackTrace = "27";

    return Arrays.asList(new String[]{valueOfArgument0, resultOfResolveFilePathArgs0,toStringOfObjectMapper, functionNameFromTestFileInStackTrace,lineNumberFromTestFileInStackTrace});
  
  }
   // TODO: CRIO_TASK_MODULE_REST_API
  //  Find out the closing price of each stock on the end_date and return the list
  //  of all symbols in ascending order by its close value on end date.

  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  //    and deserialize the results in List<Candle>

  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {
   
    final String tiingoToken = "c010f4bd4369796a57865b9a9e48b2663c9de69f";
   
    List<PortfolioTrade> portfolioTrades = readTradesFromJson(args[0]);
    
    LocalDate endDate = LocalDate.parse(args[1]);
   
    RestTemplate restTemplate = new RestTemplate();
    
    List<com.crio.warmup.stock.dto.TotalReturnsDto> totalReturnsDtos = new ArrayList<>();
   
    List<String> listOfSortSymbolsOnClosingPrice = new ArrayList<>();
   
    for (PortfolioTrade portfolioTrade : portfolioTrades) {
    
      String tiingoURL = prepareUrl(portfolioTrade, endDate, tiingoToken);
     
      TiingoCandle[] tiingoCandleArray = restTemplate.getForObject(tiingoURL, TiingoCandle[].class);
     
      totalReturnsDtos.add(new com.crio.warmup.stock.dto.TotalReturnsDto(portfolioTrade.getSymbol(),
          tiingoCandleArray[tiingoCandleArray.length - 1].getClose()));

    }
   
    Collections.sort(totalReturnsDtos,(a, b) -> Double.compare(a.getClosingPrice(), b.getClosingPrice()));
    
        for (com.crio.warmup.stock.dto.TotalReturnsDto totalReturnsDto : totalReturnsDtos) {
      
          listOfSortSymbolsOnClosingPrice.add(totalReturnsDto.getSymbol());

    }
    
    return listOfSortSymbolsOnClosingPrice;

  }
  // TODO:
 //  After refactor, make sure that the tests pass by using these two commands
 //  ./gradlew test --tests PortfolioManagerApplicationTest.readTradesFromJson
 //  ./gradlew test --tests PortfolioManagerApplicationTest.mainReadFile
 public static List<PortfolioTrade> readTradesFromJson(String filename) throws IOException, URISyntaxException {

  ObjectMapper objectMapper = getObjectMapper();

  File file = resolveFileFromResources(filename);

  return Arrays.asList(objectMapper.readValue(file, PortfolioTrade[].class));

}
// TODO:
//  Build the Url using given parameters and use this function in your code to cann the API.
public static String prepareUrl(PortfolioTrade trade, LocalDate endDate, String token) {

return "https://api.tiingo.com/tiingo/daily/" + trade.getSymbol() + "/prices?startDate="+ trade.getPurchaseDate() + "&endDate=" + endDate + "&token=" + token;

}
  
  public static String getToken() {

    return "c010f4bd4369796a57865b9a9e48b2663c9de69f";
    
    }

  public static Double getOpeningPriceOnStartDate(List<Candle> candles) {

      return candles.get(0).getOpen();
      
      }

  public static Double getClosingPriceOnEndDate(List<Candle> candles) {

        return candles.get(candles.size()-1).getClose();
        
        }

        public static List<Candle> fetchCandles(PortfolioTrade trade, LocalDate endDate, String token) {

          RestTemplate restTemplate = new RestTemplate();
        
          String tiingoRestURL = prepareUrl(trade, endDate, token);
        
          TiingoCandle[] tiingoCandleArray = restTemplate.getForObject(tiingoRestURL, TiingoCandle[].class);
             
          return Arrays.stream(tiingoCandleArray).collect(Collectors.toList());
        
   }

  
  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)  throws IOException, URISyntaxException{
  
          List<PortfolioTrade> portfolioTrades = readTradesFromJson(args[0]);
         
          List<AnnualizedReturn> annualizedReturns = new ArrayList<>();
         
          LocalDate localDate = LocalDate.parse(args[1]);
         
          for (PortfolioTrade portfolioTrade : portfolioTrades) {
            
            List<Candle> candles = fetchCandles(portfolioTrade, localDate, getToken());
            
            AnnualizedReturn annualizedReturn = calculateAnnualizedReturns(localDate, portfolioTrade, getOpeningPriceOnStartDate(candles), getClosingPriceOnEndDate(candles));
               
             annualizedReturns.add(annualizedReturn);
        
          }
        
          return annualizedReturns.stream().sorted((a1, a2) -> Double.compare(a2.getAnnualizedReturn(), a1.getAnnualizedReturn())).collect(Collectors.toList());  
              
        }      
 // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  // Return the populated list of AnnualizedReturn for all stocks.
  // Annualized returns should be calculated in two steps:
  // 1. Calculate totalReturn = (sell_value - buy_value) / buy_value.
  // 1.1 Store the same as totalReturns
  // 2. Calculate extrapolated annualized returns by scaling the same in years span.
  // The formula is:
  // annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  // 2.1 Store the same as annualized_returns
  // Test the same using below specified command. The build should be successful.
  // ./gradlew test --tests PortfolioManagerApplicationTest.testCalculateAnnualizedReturn
  
  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate, PortfolioTrade trade,double buyPrice, double sellPrice) {

    double total_num_years = ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate) / 365.2422;
   
    double totalReturns = (sellPrice - buyPrice) / buyPrice;
   
    double annualized_returns = Math.pow((1.0 + totalReturns), (1.0 / total_num_years)) - 1;
   
    return new AnnualizedReturn(trade.getSymbol(), annualized_returns, totalReturns);
  
  }

// TODO: CRIO_TASK_MODULE_REFACTOR
// Once you are done with the implementation inside PortfolioManagerImpl and
// PortfolioManagerFactory, create PortfolioManager using PortfolioManagerFactory.
// Refer to the code from previous modules to get the List<PortfolioTrades> and endDate, and
// call the newly implemented method in PortfolioManager to calculate the annualized returns.

// Note:
// Remember to confirm that you are getting same results for annualized returns as in Module 3.

private static String readFileAsString(String fileName) throws IOException, URISyntaxException {
  
  return new String(Files.readAllBytes(resolveFileFromResources(fileName).toPath()), "UTF-8");

}

  public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args)
      throws Exception {

      RestTemplate restTemplate = new RestTemplate();

       String file = args[0];

       LocalDate endDate = LocalDate.parse(args[1]);

       String contents = readFileAsString(file);

       ObjectMapper objectMapper = getObjectMapper();

       PortfolioManager portfolioManager = PortfolioManagerFactory.getPortfolioManager(restTemplate);

       PortfolioTrade[] portfolioTrades = objectMapper.readValue(contents, PortfolioTrade[].class);
       
         return portfolioManager.calculateAnnualizedReturn(Arrays.asList(portfolioTrades), endDate);
 }

 

  public static void main(String[] args) throws Exception {

    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    
    ThreadContext.put("runId", UUID.randomUUID().toString());

    printJsonObject(mainReadFile(args));

    printJsonObject(mainReadQuotes(args));

    printJsonObject(mainReadQuotes(new String[]{"trades.json" ,"2020-01-01"}));

    printJsonObject(mainCalculateSingleReturn(new String[] {"trades.json", "2020-01-01"}));

    printJsonObject(mainCalculateReturnsAfterRefactor(args));
  
  }
}





















//   public static void main(String[] args) throws Exception {
//     Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
//     ThreadContext.put("runId", UUID.randomUUID().toString());




//   }
// }

 

 
//}
// public class PortfolioManagerApplication {


  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Once you are done with the implementation inside PortfolioManagerImpl and
  //  PortfolioManagerFactory, create PortfolioManager using PortfolioManagerFactory.
  //  Refer to the code from previous modules to get the List<PortfolioTrades> and endDate, and
  //  call the newly implemented method in PortfolioManager to calculate the annualized returns.

  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.

//   public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args)
//       throws Exception {
//        String file = args[0];
//        LocalDate endDate = LocalDate.parse(args[1]);
//        String contents = readFileAsString(file);
//        ObjectMapper objectMapper = getObjectMapper();
//        return portfolioManager.calculateAnnualizedReturn(Arrays.asList(portfolioTrades), endDate);
//  // }


  // public static void main(String[] args) throws Exception {
  //   Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
  //   ThreadContext.put("runId", UUID.randomUUID().toString());




    // printJsonObject(mainCalculateReturnsAfterRefactor(args));
//   }
// }

