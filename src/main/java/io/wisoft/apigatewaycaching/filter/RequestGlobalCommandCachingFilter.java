package io.wisoft.apigatewaycaching.filter;

import io.wisoft.apigatewaycaching.service.CommandCachingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestGlobalCommandCachingFilter implements GlobalFilter {

  private final CommandCachingService commandCachingService;

  @Override
  public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
    log.info("filtered command caching");
    return commandCachingService.requestHandle(exchange, chain);
  }

}
