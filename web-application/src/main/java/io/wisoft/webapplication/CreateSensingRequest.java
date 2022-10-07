package io.wisoft.webapplication;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class CreateSensingRequest {

  private List<SensingDto> sensingList;

}
