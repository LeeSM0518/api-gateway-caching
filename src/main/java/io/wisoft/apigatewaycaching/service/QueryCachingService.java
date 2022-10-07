package io.wisoft.apigatewaycaching.service;

import io.wisoft.apigatewaycaching.cache.CacheRepository;
import io.wisoft.apigatewaycaching.service.vo.QueryCachingEvent;
import io.wisoft.apigatewaycaching.service.vo.GatewayCacheControl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;


@Slf4j
@Service
@RequiredArgsConstructor
public class QueryCachingService {

  private final CacheRepository cacheRepository;
  private final AutoCacheUpdater autoCacheUpdater;

  public Mono<Void> requestHandle(final ServerWebExchange exchange, final GatewayFilterChain chain) {
    if (CacheHeaderValidator.isQueryCaching(exchange.getRequest().getHeaders())) {
      final String requestPath = exchange.getRequest().getPath().value();

      log.info("query request caching start");
      final String cache = cacheRepository.findQueryCache(requestPath);
      log.info("query request caching end");

      if (cache != null) {
        final byte[] cacheBytes = cache.getBytes(StandardCharsets.UTF_8);
        final DataBuffer cacheBuffer = exchange.getResponse().bufferFactory().wrap(cacheBytes);
        exchange.getResponse().getHeaders().set(HttpHeaders.CONTENT_TYPE, "application/json");
        return exchange.getResponse().writeWith(Flux.just(cacheBuffer));
      }
    }
    return chain.filter(exchange);
  }

  public Mono<Void> responseHandle(final ServerWebExchange exchange, final GatewayFilterChain chain) {
    if (CacheHeaderValidator.isQueryCaching(exchange.getRequest().getHeaders())) {
      ServerHttpResponseDecorator responseDecorator = getDecoratedResponse(exchange);
      return chain.filter(exchange.mutate().response(responseDecorator).build());
    }
    return chain.filter(exchange);
  }

  private ServerHttpResponseDecorator getDecoratedResponse(final ServerWebExchange exchange) {
    final ServerHttpRequest request = exchange.getRequest();
    final ServerHttpResponse response = exchange.getResponse();
    final DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();

    return new ServerHttpResponseDecorator(response) {
      @Override
      public Mono<Void> writeWith(final Publisher<? extends DataBuffer> body) {
        if (body instanceof Flux) {
          Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
          return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
            final DefaultDataBuffer joinedBuffers = new DefaultDataBufferFactory().join(dataBuffers);
            final byte[] content = new byte[joinedBuffers.readableByteCount()];
            joinedBuffers.read(content);

            final String responseBody = new String(content, UTF_8);
            final RequestPath requestPath = request.getPath();
            log.info("requestId: {}, method: {}, url: {}", request.getId(), request.getMethodValue(), requestPath);
            Optional<GatewayCacheControl> control = GatewayCacheControl.create(request.getHeaders());
            cacheRepository.saveQueryCache(requestPath.toString(), responseBody);
            autoCacheUpdater.issue(new QueryCachingEvent(requestPath.toString(), control.get(), responseBody));

            return bufferFactory.wrap(responseBody.getBytes());
          })).onErrorResume(err -> {
            log.error("error while decorating Reponse : {}", err.getMessage());
            return Mono.empty();
          });
        }
        return super.writeWith(body);
      }
    };
  }

}
