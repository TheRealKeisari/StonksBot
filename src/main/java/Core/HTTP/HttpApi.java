package Core.HTTP;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

/**
 * Tiny Api for Core.HTTP Get requests
 * @author etsubu
 * @version 28 Aug 2018
 *
 */
public class HttpApi {
    private static final Logger log = LoggerFactory.getLogger(HttpApi.class);
    private static final long TIMEOUT = 30;
    private static final HttpClient client;

    static  {
        client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(TIMEOUT)).build();
    }
    /**
     * Sends Core.HTTP Get request and returns the response in String
     * @param url URL to request
     * @return Response in String
     * @throws IOException If there was an connection error
     */
    public static Optional<String> sendGet(String url) throws IOException, InterruptedException {

        log.info("Sending GET request to URL {}", url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
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
}
