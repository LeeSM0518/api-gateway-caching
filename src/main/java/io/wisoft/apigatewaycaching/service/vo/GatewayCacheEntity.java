package io.wisoft.apigatewaycaching.service.vo;

import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Optional;

public class GatewayCacheEntity {

  private static final String KEY = "Gateway-Cache-Entity";

  private final String value;

  private GatewayCacheEntity(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static Optional<GatewayCacheEntity> create(final HttpHeaders headers) {
    final List<String> cacheEntities = headers.get(KEY);
    if (cacheEntities == null || cacheEntities.size() != 1) return Optional.empty();

    final String cacheEntity = cacheEntities.get(0);
    final GatewayCacheEntity gatewayCacheEntity = new GatewayCacheEntity(cacheEntity);
    return Optional.of(gatewayCacheEntity);
  }

}
