package io.wisoft.apigatewaycaching.service;

import io.wisoft.apigatewaycaching.cache.CacheRepository;
import io.wisoft.apigatewaycaching.service.vo.QueryCachingEvent;
import io.wisoft.apigatewaycaching.scheduler.DynamicScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoCacheUpdater {

  private final DynamicScheduler dynamicScheduler;
  private final HttpRequester requester;
  private final CacheRepository repository;

  void issue(QueryCachingEvent event) {
    int intervalPeriod = event.getControl().getValue();
    String requestPath = event.getRequestPath();
    dynamicScheduler.scheduleQueryCaching(requestPath, intervalPeriod, () -> {
      log.info("query caching : request -> {}", requestPath);
      String response = requester.get(requestPath);
      repository.saveQueryCache(requestPath, response);
    });
  }

}
