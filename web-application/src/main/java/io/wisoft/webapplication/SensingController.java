package io.wisoft.webapplication;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sensing/data")
public class SensingController {

  private final SensingRepository sensingRepository;

  @GetMapping
  public List<Sensing> getAllSensingData() {
    return sensingRepository.findAll();
  }

  @PostMapping
  public String postAllSensingData(@RequestBody CreateSensingRequest dto) {
    int savedSensingCount = sensingRepository
        .saveAll(
            dto.getSensingList()
                .stream()
                .map(sensing -> Sensing.create(sensing.value))
                .collect(toList()))
        .size();
    return "200 OK - count : " + savedSensingCount;
  }

}
