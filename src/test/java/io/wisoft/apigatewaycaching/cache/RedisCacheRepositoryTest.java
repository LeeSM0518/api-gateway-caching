package io.wisoft.apigatewaycaching.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RedisCacheRepositoryTest {

  @Autowired
  CacheRepository cacheRepository;

  @BeforeEach
  void setup() {
    cacheRepository.delete("key");
  }

  @Test
  void saveAndFindTest() {
    final String key = "key";
    final String expected = "value";

    cacheRepository.save(key, expected);
    final String actual = cacheRepository.find(key);

    assertThat(actual).isEqualTo(expected);
  }

}