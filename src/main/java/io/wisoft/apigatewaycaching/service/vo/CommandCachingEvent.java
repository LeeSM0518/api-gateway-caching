package io.wisoft.apigatewaycaching.service.vo;

import lombok.Value;

@Value
public class CommandCachingEvent {

  String requestPath;
  GatewayCacheControl control;
  GatewayCacheEntity entity;

}
