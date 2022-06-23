package io.wisoft.apigatewaycaching.filter.service;

import io.wisoft.apigatewaycaching.cache.CacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

import static io.wisoft.apigatewaycaching.filter.service.CacheHeaderValidator.isValid;


@Slf4j
@Service
@RequiredArgsConstructor
public class QueryCachingService {

  private final CacheRepository cacheRepository;

  public Mono<Void> handle(final ServerWebExchange exchange, final GatewayFilterChain chain) {
    if (isValid(exchange.getRequest().getHeaders())) {
      final String requestPath = exchange.getRequest().getPath().value();

      final String cache = cacheRepository.find(requestPath);

      if (cache != null) {
        final byte[] cacheBytes = cache.getBytes(StandardCharsets.UTF_8);
        final DataBuffer cacheBuffer = exchange.getResponse().bufferFactory().wrap(cacheBytes);
        exchange.getResponse().getHeaders().set(HttpHeaders.CONTENT_TYPE, "application/json");
        return exchange.getResponse().writeWith(Flux.just(cacheBuffer));
      }
    }
    return chain.filter(exchange);
  }

}
