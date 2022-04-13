package io.wisoft.apigatewaycaching.cache;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RedisTest {

  @Autowired
  StringRedisTemplate redisTemplate;

  @Test
  public void testStrings() {
    final String key = "test";
    final String expected = "1";

    final ValueOperations<String, String> operations = redisTemplate.opsForValue();

    operations.set(key, expected);
    final String actual = operations.get(key);

    assertThat(actual).isEqualTo(expected);
  }

}
