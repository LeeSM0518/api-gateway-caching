package io.wisoft.apigatewaycaching.filter;

import io.wisoft.apigatewaycaching.cache.CacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResponseGlobalFilter implements GlobalFilter, Ordered {

  private final CacheRepository cacheRepository;

  @Override
  public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
    final ServerHttpResponseDecorator decoratedResponse = getDecoratedResponse(exchange);

    return chain.filter(exchange.mutate().response(decoratedResponse).build());
  }

  private ServerHttpResponseDecorator getDecoratedResponse(final ServerWebExchange exchange) {

    final ServerHttpResponse response = exchange.getResponse();
    return new ServerHttpResponseDecorator(response) {
      @Override
      public Mono<Void> writeWith(final Publisher<? extends DataBuffer> body) {
        final String path = exchange.getRequest().getPath().toString();
        final ServerHttpRequest request = exchange.getRequest();
        final DataBufferFactory dataBufferFactory = response.bufferFactory();

        final HttpHeaders headers = request.getHeaders();

        final List<String> gatewayCacheControls = headers.get("Gateway-Cache-Control");
        final List<String> gatewayCacheMethods = headers.get("Gateway-Cache-Method");

        if (gatewayCacheMethods == null || gatewayCacheControls == null) {
          log.info("Uncached Response");
          return super.writeWith(body);
        }

        if (gatewayCacheControls.size() != 1 || gatewayCacheMethods.size() != 1) {
          log.error("캐시 헤더의 개수 부족 예외 발생");
          log.error("gatewayCacheControls : {}, gatewayCacheMethods: {}", gatewayCacheControls, gatewayCacheMethods);
          throw new IllegalArgumentException("캐시 헤더의 개수 부족 예외 발생");
        }

        final String gatewayCacheControl = gatewayCacheControls.get(0);
        final String gatewayCacheMethod = gatewayCacheMethods.get(0);

        log.info("gatewayCacheControls : {}", gatewayCacheControl);
        log.info("Cache Method : {}", gatewayCacheMethod);

        if (gatewayCacheMethod.equals("command"))
          return super.writeWith(body);

        if (body instanceof Flux) {
          Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
          return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
            final DefaultDataBuffer joinedBuffers = new DefaultDataBufferFactory().join(dataBuffers);
            final byte[] content = new byte[joinedBuffers.readableByteCount()];
            joinedBuffers.read(content);

            final String responseBody = new String(content, UTF_8);
            final RequestPath requestPath = request.getPath();
            log.info("requestId: {}, method: {}, url: {}", request.getId(), request.getMethodValue(), requestPath);

            cacheRepository.save(requestPath.toString(), responseBody);

            return dataBufferFactory.wrap(responseBody.getBytes());
          })).onErrorResume(err -> {
            log.error("error while decorating Reponse : {}", err.getMessage());
            return Mono.empty();
          });
        }
        return super.writeWith(body);
      }
    };
  }

  @Override
  public int getOrder() {
    return -2;
  }

}
