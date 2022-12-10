package io.wisoft.apigatewaycaching.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.wisoft.apigatewaycaching.cache.CacheRepository;
import io.wisoft.apigatewaycaching.scheduler.DynamicScheduler;
import io.wisoft.apigatewaycaching.service.vo.CommandCachingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheSynchronizer {

  private final DynamicScheduler dynamicScheduler;
  private final HttpRequester requester;
  private final CacheRepository repository;
  private final ObjectMapper mapper;

  void issue(CommandCachingEvent event) {
    int intervalPeriod = event.getControl().getValue();
    String requestPath = event.getRequestPath();
    dynamicScheduler.scheduleCommandCaching(requestPath, intervalPeriod, () -> {
      String caches = repository.findCommandCache(requestPath).toString();
      try {
        List parsedCaches = mapper.readValue(caches, List.class);
        Map<String, List> requestBody = Map.of(event.getEntity().getValue(), parsedCaches);
        String json = mapper.writeValueAsString(requestBody);
        try {
          requester.post(requestPath, json);
        } catch (Exception e) {
          log.error("error while post cache : request path -> {} , cache -> {}", requestPath, json);
        }
        try {
          repository.clearCommandCache(requestPath);
        } catch (Exception e) {
          log.error("error while delete: key -> {}", requestPath);
          e.printStackTrace();
        }
      } catch (JsonProcessingException e) {
        log.error("error while json parsing for command caching : cache -> {}", caches);
        throw new RuntimeException(e);
      }
    });
  }

}
