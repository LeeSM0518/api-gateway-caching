package io.wisoft.apigatewaycaching.filter.service.vo;

import io.wisoft.apigatewaycaching.service.vo.GatewayCacheControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class GatewayCacheControlTest {

  private static final String KEY = "Gateway-Cache-Control";

  private HttpHeaders mockHeaders;

  @BeforeEach
  void setup() {
    mockHeaders = mock(HttpHeaders.class);
  }

  @Test
  void successCreateTest() {
    final int expectedValue = 120;
    given(mockHeaders.get(KEY)).willReturn(List.of("max-age=" + expectedValue));

    final Optional<GatewayCacheControl> gatewayCacheControl = GatewayCacheControl.create(mockHeaders);

    assertThat(gatewayCacheControl.isPresent()).isTrue();
    assertThat(gatewayCacheControl.get().getValue()).isEqualTo(expectedValue);
  }

}