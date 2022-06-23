package io.wisoft.apigatewaycaching.filter.service;

import io.wisoft.apigatewaycaching.filter.service.vo.GatewayCacheControl;
import io.wisoft.apigatewaycaching.filter.service.vo.GatewayCacheMethod;
import org.springframework.http.HttpHeaders;

import java.util.Optional;

public class CacheHeaderValidator {

  private static final String QUERY_METHOD = "query";

  public static boolean isValid(final HttpHeaders headers) {
    final Optional<GatewayCacheControl> control = GatewayCacheControl.create(headers);
    final Optional<GatewayCacheMethod> method = GatewayCacheMethod.create(headers);
    return method.isPresent() && control.isPresent() && method.get().getValue().equals(QUERY_METHOD);
  }

}
