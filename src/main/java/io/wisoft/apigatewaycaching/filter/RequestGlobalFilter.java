package io.wisoft.apigatewaycaching.filter;

import io.wisoft.apigatewaycaching.service.QueryCachingService;
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
public class RequestGlobalFilter implements GlobalFilter {

  private final QueryCachingService queryCachingService;

  @Override
  public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
    return queryCachingService.requestHandle(exchange, chain);
  }

}
