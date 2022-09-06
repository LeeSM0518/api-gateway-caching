package io.wisoft.apigatewaycaching.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Slf4j
@Component
public class HttpRequester {

  @Value("${spring.application.host}")
  private String requestHost;

  private final RequestConfig config = RequestConfig.DEFAULT;

  public String get(String requestPath) {
    CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

    try (client) {
      HttpGet request = new HttpGet(requestHost + requestPath);

//      request.addHeader("content-type", );
      try (CloseableHttpResponse response = client.execute(request)) {
        HttpEntity entity = response.getEntity();
        InputStream content = entity.getContent();
        InputStreamReader streamReader = new InputStreamReader(content, StandardCharsets.UTF_8);
        return new BufferedReader(streamReader).lines().collect(Collectors.joining("\n"));
      } catch (Exception e) {
        log.error("Request get fail, request Path is " + requestPath);
        throw new RuntimeException(e);
      }

    } catch (IOException e) {
      log.error("Creating client fail, requestPath is " + requestPath);
      throw new RuntimeException(e);
    }
  }

}
