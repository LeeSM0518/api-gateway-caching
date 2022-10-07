package io.wisoft.apigatewaycaching.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static java.util.stream.Collectors.joining;
import static org.springframework.http.HttpStatus.OK;

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

      try (CloseableHttpResponse response = client.execute(request)) {
        HttpEntity entity = response.getEntity();
        InputStream content = entity.getContent();
        InputStreamReader streamReader = new InputStreamReader(content, StandardCharsets.UTF_8);
        return new BufferedReader(streamReader).lines().collect(joining("\n"));
      } catch (Exception e) {
        log.error("Request get fail, request path is " + requestPath);
        throw new RuntimeException(e);
      }

    } catch (IOException e) {
      log.error("Creating client fail, request path is " + requestPath);
      throw new RuntimeException(e);
    }
  }

  public String post(String requestPath, String body) {
    CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

    try (client) {
      HttpPost request = new HttpPost(requestHost + requestPath);
      StringEntity requestEntity = new StringEntity(body);
      request.setEntity(requestEntity);
      request.setHeader("Content-Type", "application/json");

      try (CloseableHttpResponse response = client.execute(request)) {
        if (response.getStatusLine().getStatusCode() != OK.value()) throw new Exception();
        HttpEntity responseEntity = response.getEntity();
        InputStream content = responseEntity.getContent();
        InputStreamReader streamReader = new InputStreamReader(content, StandardCharsets.UTF_8);
        return new BufferedReader(streamReader).lines().collect(joining("\n"));
      } catch (Exception e) {
        log.error("Request post fail, request path is " + requestPath);
        throw new RuntimeException(e);
      }

    } catch (Exception e) {
      log.error("Creating client fail, request path is " + requestPath);
      throw new RuntimeException(e);
    }
  }

}
