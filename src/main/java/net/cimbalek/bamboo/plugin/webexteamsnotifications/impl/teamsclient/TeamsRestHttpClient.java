package net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.teamsclient;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static java.util.Objects.requireNonNull;

/**
 * HTTP client taking care of requests to Webex Teams API
 */
@Service
public class TeamsRestHttpClient {
    private static final Logger logger = LoggerFactory.getLogger(TeamsRestHttpClient.class);

    private static final String API_URL = "https://api.ciscospark.com/v1/";

    private final RestTemplate restTemplate;

    /**
     * Constructs new instance of {@link TeamsRestHttpClient} with initialized rest template
     */
    public TeamsRestHttpClient() {
        final CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();
        final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        restTemplate = new RestTemplate(requestFactory);
    }

    /**
     * Sends GET request to API and waits for response
     *
     * @param url
     *         resource URL
     * @param botToken
     *         authentication token
     * @param responseClass
     *         class of response
     * @param <T>
     *         type of response
     * @return new instance of wrapped response entity
     */
    @NotNull
    public <T> ResponseEntity<T> sendGetRequest(@NotNull final String url, @NotNull final String botToken, @NotNull final Class<T> responseClass) {
        requireNonNull(url, "'url' cannot be null");
        requireNonNull(botToken, "'botToken' cannot be null");
        requireNonNull(responseClass, "'responseClass' cannot be null");

        logger.debug("Sending GET request to URL '{}', with bot token '{}' expecting class '{}'.", url, botToken, responseClass);
        final HttpEntity<String> requestHttpEntity = new HttpEntity<>(null, buildHttpHeaders(botToken));
        final ResponseEntity<T> result = restTemplate.exchange(API_URL + url, HttpMethod.GET, requestHttpEntity, responseClass);
        logger.debug("Received GET response '{}'", result);
        return result;
    }

    /**
     * Sends POST request to API and waits for response
     *
     * @param url
     *         resource URL
     * @param botToken
     *         authentication token
     * @param responseClass
     *         class of response
     * @param <T>
     *         type of response
     * @param request
     *         body of the request
     * @return new instance of wrapped response entity
     */
    @NotNull
    public <T, U> ResponseEntity<T> sendPostRequest(@NotNull final String url, @NotNull final String botToken, @NotNull final Class<T> responseClass,
                                                    final U request) {
        requireNonNull(url, "'url' cannot be null");
        requireNonNull(botToken, "'botToken' cannot be null");
        requireNonNull(responseClass, "'responseClass' cannot be null");
        requireNonNull(request, "'request' cannot be null");

        logger.debug("Sending POST body '{}' to URL '{}', with bot token '{}' expecting class '{}'.", request, url, botToken, responseClass);
        final HttpEntity<U> requestEntity = new HttpEntity<>(request, buildHttpHeaders(botToken));
        final ResponseEntity<T> result = restTemplate.exchange(API_URL + url, HttpMethod.POST, requestEntity, responseClass);
        logger.debug("Received POST response '{}'", result);
        return result;
    }

    private HttpHeaders buildHttpHeaders(final String botToken) {
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", String.format("Bearer %s", botToken));
        headers.add("Content-type", "application/json; charset=utf-8");
        return headers;
    }
}
