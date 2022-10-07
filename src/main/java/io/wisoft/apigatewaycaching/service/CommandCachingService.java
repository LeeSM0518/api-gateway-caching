package io.wisoft.apigatewaycaching.service;

import io.wisoft.apigatewaycaching.cache.CacheRepository;
import io.wisoft.apigatewaycaching.service.vo.CommandCachingEvent;
import io.wisoft.apigatewaycaching.service.vo.GatewayCacheControl;
import io.wisoft.apigatewaycaching.service.vo.GatewayCacheEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommandCachingService {

  private final CacheRepository cacheRepository;
  private final CacheSynchronizer cacheSynchronizer;

  public Mono<Void> requestHandle(final ServerWebExchange exchange, final GatewayFilterChain chain) {
    log.info("before command caching");
    if (CacheHeaderValidator.isCommandCaching(exchange.getRequest().getHeaders())) {
      log.info("start command caching");
      ServerHttpRequest request = exchange.getRequest();
      Flux<DataBuffer> body = exchange.getRequest().getBody();

      body.buffer().subscribe(dataBuffers -> {
        log.info("process request body");
        final DefaultDataBuffer joinedBuffers = new DefaultDataBufferFactory().join(dataBuffers);
        final byte[] content = new byte[joinedBuffers.readableByteCount()];
        joinedBuffers.read(content);

        String requestBody = new String(content, UTF_8);
        RequestPath requestPath = request.getPath();
        log.info("requestId: {}, method: {}, url: {}, cache: {}", request.getId(), request.getMethodValue(), requestPath, requestBody);

        Optional<GatewayCacheControl> control = GatewayCacheControl.create(request.getHeaders());
        Optional<GatewayCacheEntity> entity = GatewayCacheEntity.create(request.getHeaders());

        String jsonBody = requestBody.replace("\n", "").trim();

        cacheRepository.saveCommandCache(requestPath.toString(), jsonBody);
        cacheSynchronizer.issue(new CommandCachingEvent(requestPath.toString(), control.get(), entity.get()));
      });

      log.info("end command caching");
      byte[] response = "OK".getBytes(UTF_8);
      DataBuffer wrap = exchange.getResponse().bufferFactory().wrap(response);
      return exchange.getResponse().writeWith(Flux.just(wrap));
    }
    return chain.filter(exchange);
  }

}
