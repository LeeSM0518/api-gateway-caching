package io.wisoft.apigatewaycaching.filter;

import io.wisoft.apigatewaycaching.cache.CacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestGlobalFilter implements GlobalFilter {

  private final CacheRepository cacheRepository;

  @Override
  public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
    final HttpHeaders headers = exchange.getRequest().getHeaders();

    final List<String> gatewayCacheControls = headers.get("Gateway-Cache-Control");
    final List<String> gatewayCacheMethods = headers.get("Gateway-Cache-Method");

    if (gatewayCacheMethods == null || gatewayCacheControls == null) {
      log.info("Uncached Request");
      return chain.filter(exchange);
    }

    final String gatewayCacheControl = gatewayCacheControls.get(0);
    final String gatewayCacheMethod = gatewayCacheMethods.get(0);

    log.info("Cache Control -> {}", gatewayCacheControl);
    log.info("Cache Method -> {}", gatewayCacheMethod);

    if (gatewayCacheMethod.equals("query")) {
      final String requestPath = exchange.getRequest().getPath().value();

      log.info("Request path -> {}", requestPath);

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
