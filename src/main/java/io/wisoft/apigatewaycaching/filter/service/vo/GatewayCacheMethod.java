package io.wisoft.apigatewaycaching.filter.service.vo;

import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Optional;

public class GatewayCacheMethod {

  private static final String KEY = "Gateway-Cache-Method";
  private final String value;

  private GatewayCacheMethod(final String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static Optional<GatewayCacheMethod> create(final HttpHeaders headers) {
    final List<String> cacheMethods = headers.get(KEY);
    if (cacheMethods == null || cacheMethods.size() != 1) return Optional.empty();

    final String cacheMethod = cacheMethods.get(0);
    if (!(cacheMethod.equals("query") || cacheMethod.equals("command"))) {
      return Optional.empty();
    }

    final GatewayCacheMethod gatewayCacheMethod = new GatewayCacheMethod(cacheMethod);
    return Optional.of(gatewayCacheMethod);
  }

}
