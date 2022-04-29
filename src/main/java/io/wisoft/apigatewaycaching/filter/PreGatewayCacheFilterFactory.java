package io.wisoft.apigatewaycaching.filter;

import io.wisoft.apigatewaycaching.cache.CacheRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.RequestPath;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Component
@Slf4j
public class PreGatewayCacheFilterFactory extends AbstractGatewayFilterFactory<PreGatewayCacheFilterFactory.Config> {

  private final CacheRepository cacheRepository;

  public PreGatewayCacheFilterFactory(@Autowired final CacheRepository cacheRepository) {
    super(Config.class);
    this.cacheRepository = cacheRepository;
  }

  @Override
  public GatewayFilter apply(final Config config) {
    return ((exchange, chain) -> {
      final HttpHeaders headers = exchange.getRequest().getHeaders();
      final String gatewayCacheControl = Objects.requireNonNull(headers.get("Gateway-Cache-Control")).get(0);
      final String gatewayCacheMethod = Objects.requireNonNull(headers.get("Gateway-Cache-Method")).get(0);

      log.info("PreGatewayCacheFilterFactory : Cache Control -> {}", gatewayCacheControl);
      log.info("PreGatewayCacheFilterFactory : Cache Method -> {}", gatewayCacheMethod);

      if (gatewayCacheMethod.equals("query")) {
        final String requestPath = exchange.getRequest().getPath().value();

        log.info("PreGatewayCacheFilterFactory : Request path -> {}", requestPath);

        final String cache = cacheRepository.find(requestPath);

        if (cache != null) {
          final byte[] cacheBytes = cache.getBytes(StandardCharsets.UTF_8);
          final DataBuffer cacheBuffer = exchange.getResponse().bufferFactory().wrap(cacheBytes);
          return exchange.getResponse().writeWith(Flux.just(cacheBuffer));
        }
      }
      return chain.filter(exchange);
    });
  }

  public static class Config {

  }

}
