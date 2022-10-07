package io.wisoft.apigatewaycaching.service;

import io.wisoft.apigatewaycaching.service.vo.GatewayCacheControl;
import io.wisoft.apigatewaycaching.service.vo.GatewayCacheEntity;
import io.wisoft.apigatewaycaching.service.vo.GatewayCacheMethod;
import org.springframework.http.HttpHeaders;

import java.util.Optional;

public class CacheHeaderValidator {

  public static final String QUERY_METHOD = "query";
  public static final String COMMAND_METHOD = "command";

  public static boolean isQueryCaching(final HttpHeaders headers) {
    return isCaching(headers, QUERY_METHOD);
  }

  public static boolean isCommandCaching(final HttpHeaders headers) {
    final Optional<GatewayCacheEntity> entity = GatewayCacheEntity.create(headers);
    return isCaching(headers, COMMAND_METHOD) && entity.isPresent();
  }

  private static boolean isCaching(HttpHeaders headers, String methodString) {
    final Optional<GatewayCacheControl> control = GatewayCacheControl.create(headers);
    final Optional<GatewayCacheMethod> method = GatewayCacheMethod.create(headers);
    return method.isPresent() && control.isPresent() && method.get().getValue().equals(methodString);
  }

}
