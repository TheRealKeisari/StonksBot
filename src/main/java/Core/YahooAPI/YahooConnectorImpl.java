package Core.YahooAPI;

import Core.YahooAPI.DataStructures.AssetPriceIntraInfo;
import Core.YahooAPI.DataStructures.DataResponse;
import Core.YahooAPI.DataStructures.GeneralResponse;
import Core.YahooAPI.DataStructures.PriceChart.PriceMeta.ChartResult;
import Core.YahooAPI.DataStructures.SearchStructures.SearchResponse;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

@Component
public class YahooConnectorImpl implements YahooConnector{
    private static final Logger log = LoggerFactory.getLogger(YahooConnectorImpl.class);
    private static final String FUNDAMENT_BASE_URL = "https://query%d.finance.yahoo.com/v10/finance/quoteSummary/%s?modules=%s";
    private static final String PRICE_BASE_URL = "https://query%d.finance.yahoo.com/v8/finance/chart/%s?interval=1d&range=2d";
    private static final String SEARCH_BASE_URL = "https://query%d.finance.yahoo.com/v1/finance/search?q=%s&quotesCount=1&enableFuzzyQuery=false&quotesQueryId=tss_match_phrase_query";
    private static final String DEFAULT_STATISTICS = "defaultKeyStatistics";
    public static final String CALENDAR_EVENTS = "calendarEvents";
    private final HttpClient client;
    private int loadBalanceIndex;
    private final TickerStorage tickerStorage;
    private final Gson gson;

    public YahooConnectorImpl(TickerStorage tickerStorage) {
        client = HttpClient.newHttpClient();
        loadBalanceIndex = 1;
        gson = new Gson();
        this.tickerStorage = tickerStorage;
    }

    private int getLoadBalanceIndex() {
        int index = loadBalanceIndex;
        loadBalanceIndex = (loadBalanceIndex == 1) ? 2 : 1;
        return index;
    }

    private Optional<String> requestHttp(String url) throws IOException, InterruptedException {
        URI uri = URI.create(url);
        log.info("Sending request to {}", uri.toString());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() != 200) {
            log.error("Request returned invalid status code {}", response.statusCode());
            return Optional.empty();
        }
        return Optional.of(response.body());
    }

    public Optional<StockName> findTicker(String keyword) throws IOException, InterruptedException {
        Optional<StockName> ticker = tickerStorage.findTicker(keyword);
        if(ticker.isPresent()) {
            log.info("Found ticker from cache {}=>{}", keyword, ticker.get());
            return ticker;
        }
        // Query from yahoo finance
        String requestUrl = String.format(SEARCH_BASE_URL, getLoadBalanceIndex(), keyword);
        Optional<String> response = requestHttp(requestUrl);
        if(response.isPresent()) {
            SearchResponse searchResults = gson.fromJson(response.get(), SearchResponse.class);
            if(searchResults.getQuotes().isEmpty()) {
                return Optional.empty();
            }
            String queriedTicker = searchResults.getQuotes().get(0).getSymbol();
            String queriedName = Optional.ofNullable(searchResults.getQuotes().get(0).getLongname()).orElse(queriedTicker);
            StockName stockName = new StockName(queriedTicker, queriedName);
            tickerStorage.setShortcut(keyword, stockName);
            return Optional.of(stockName);
        }
        return Optional.empty();
    }

    public Optional<BarSeries> queryIntraPriceChart(String ticker) throws IOException, InterruptedException {
        String requestUrl = String.format(PRICE_BASE_URL, getLoadBalanceIndex(), ticker);
        Optional<String> body = requestHttp(requestUrl);
        if(body.isPresent()) {
            return Optional.ofNullable(ChartResult.buildChartResultFromJson(body.get()).getBarSeries());
        }
        return Optional.empty();
    }

    public Optional<AssetPriceIntraInfo> queryCurrentIntraPriceInfo(String keyword) throws IOException, InterruptedException {
        StockName name = findTicker(keyword)
                .orElseThrow(() -> new IOException("Could not find any assets with keyword " + keyword));
        return queryIntraPriceChart(name.getTicker()).map(x -> new AssetPriceIntraInfo(x, name));
    }

    public Optional<DataResponse> queryData(String keyword, String... typeList) throws IOException, InterruptedException {
        StockName ticker = findTicker(keyword)
                .orElseThrow(() -> new IOException("Could not find any assets with keyword " + keyword));
        String modules = String.join("%2C", typeList);
        String requestUrl = String.format(FUNDAMENT_BASE_URL, getLoadBalanceIndex(), ticker.getTicker(), modules);
        return requestHttp(requestUrl).map(GeneralResponse::parseResponse);
    }
}
