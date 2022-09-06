package io.wisoft.apigatewaycaching.service;

import io.wisoft.apigatewaycaching.cache.CacheRepository;
import io.wisoft.apigatewaycaching.service.vo.CachingEvent;
import io.wisoft.apigatewaycaching.scheduler.DynamicScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AutoCacheUpdater {

  private final DynamicScheduler dynamicScheduler;
  private final HttpRequester requester;
  private final CacheRepository repository;

  void issue(CachingEvent event) {
    int intervalPeriod = event.getControl().getValue();
    String requestPath = event.getRequestPath();
    dynamicScheduler.scheduleATask(requestPath, intervalPeriod, () -> {
      String response = requester.get(requestPath);
      repository.saveWithExpireTime(requestPath, response, intervalPeriod);
    });
  }

}
