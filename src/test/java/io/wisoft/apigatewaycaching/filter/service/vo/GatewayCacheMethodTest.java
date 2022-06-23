package io.wisoft.apigatewaycaching.filter.service.vo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;


class GatewayCacheMethodTest {

  private static final String KEY = "Gateway-Cache-Method";

  private HttpHeaders mockHeaders;

  @BeforeEach
  void setup() {
    mockHeaders = mock(HttpHeaders.class);
  }

  @Test
  void successCreateTest() {
    final String expectedValue = "query";
    given(mockHeaders.get(KEY)).willReturn(List.of(expectedValue));

    final Optional<GatewayCacheMethod> gatewayCacheMethod = GatewayCacheMethod.create(mockHeaders);

    assertThat(gatewayCacheMethod.isPresent()).isTrue();
    assertThat(gatewayCacheMethod.get().getValue()).isEqualTo(expectedValue);
  }

  @Test
  void failCreateWithInvalidKeyTest() {
    given(mockHeaders.get(KEY)).willReturn(null);

    final Optional<GatewayCacheMethod> gatewayCacheMethod = GatewayCacheMethod.create(mockHeaders);

    assertThat(gatewayCacheMethod.isPresent()).isFalse();
  }

  @Test
  void failCreateWithInvalidValueTest() {
    given(mockHeaders.get(KEY)).willReturn(List.of("invalidValue"));

    final Optional<GatewayCacheMethod> gatewayCacheMethod = GatewayCacheMethod.create(mockHeaders);

    assertThat(gatewayCacheMethod.isPresent()).isFalse();
  }

}