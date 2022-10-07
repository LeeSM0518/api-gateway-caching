package io.wisoft.apigatewaycaching.service.vo;

import lombok.Value;

@Value
public class QueryCachingEvent {

  String requestPath;
  GatewayCacheControl control;
  String cache;

}
