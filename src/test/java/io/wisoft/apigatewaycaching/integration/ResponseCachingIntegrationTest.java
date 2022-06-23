package io.wisoft.apigatewaycaching.integration;

import io.wisoft.apigatewaycaching.cache.CacheRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ResponseCachingIntegrationTest {

  private static final String REQUEST_URL = "/sensing/data";

  @Autowired
  private WebTestClient client;

  @Autowired
  private CacheRepository cacheRepository;

  @BeforeEach
  public void setup() {
    final ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1))
        .build();

    client = client.mutate()
        .responseTimeout(Duration.ofSeconds(20))
        .exchangeStrategies(exchangeStrategies)
        .build();

    cacheRepository.delete(REQUEST_URL);
  }

  @Test
  void successTest() {
    final long startTimeBeforeCaching = System.currentTimeMillis();
    request();
    final long elapsedTimeBeforeCaching = System.currentTimeMillis() - startTimeBeforeCaching;

    final long startTimeAfterCaching = System.currentTimeMillis();
    request();
    final long elapsedTimeAfterCaching = System.currentTimeMillis() - startTimeAfterCaching;

    assertThat(elapsedTimeAfterCaching).isLessThan(elapsedTimeBeforeCaching);
  }

  private void request() {
    this.client
        .get()
        .uri(REQUEST_URL)
        .accept(APPLICATION_JSON)
        .header("Gateway-Cache-Method", "query")
        .header("Gateway-Cache-Control", "max-age=120")
        .exchange()
        .expectStatus().isOk()
        .expectHeader().valueEquals("Content-Type", "application/json")
        .expectBody(new ParameterizedTypeReference<List<TestData>>() {
        })
        .consumeWith(response -> {
          final List<TestData> responseBody = response.getResponseBody();
          assertThat(responseBody).isNotNull();
          assertThat(responseBody.size()).isEqualTo(1_000_000);
        });
  }


  private static class TestData {
    int id;
    double value;
  }

}
