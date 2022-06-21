package io.wisoft.webapplication;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SensingController {

  private final SensingRepository sensingRepository;

  @GetMapping("/sensing/data")
  public List<Sensing> getAllSensingData() {
    return sensingRepository.findAll();
  }

}
