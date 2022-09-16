package io.wisoft.apigatewaycaching.service.vo;

import lombok.Value;

@Value
public class CachingEvent {

  String requestPath;
  GatewayCacheControl control;
  String cache;

}
