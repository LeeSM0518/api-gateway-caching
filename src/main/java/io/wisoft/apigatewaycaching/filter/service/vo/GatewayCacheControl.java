package io.wisoft.apigatewaycaching.filter.service.vo;

import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Optional;

public class GatewayCacheControl {

  private static final String KEY = "Gateway-Cache-Control";

  private final int value;

  private GatewayCacheControl(final int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static Optional<GatewayCacheControl> create(final HttpHeaders headers) {
    final List<String> cacheControls = headers.get(KEY);
    if (cacheControls == null || cacheControls.size() != 1) return Optional.empty();

    final String cacheControl = cacheControls.get(0);
    final String stringValue = cacheControl.replace("max-age=", "");

    try {
      final int intValue = Integer.parseInt(stringValue);
      final GatewayCacheControl control = new GatewayCacheControl(intValue);
      return Optional.of(control);
    } catch (NumberFormatException exception) {
      return Optional.empty();
    }
  }

}
