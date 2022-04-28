package io.wisoft.apigatewaycaching.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
public class RedisCacheRepository implements CacheRepository {

  private final ValueOperations<String, String> operations;

  public RedisCacheRepository(@Autowired final StringRedisTemplate redisTemplate) {
    this.operations = redisTemplate.opsForValue();
  }

  @Override
  public String find(final String key) {
    return operations.get(key);
  }

  @Override
  public void save(final String key, final String value) {
    operations.set(key, value);
  }

  @Override
  public String delete(final String key) {
    return operations.getAndDelete(key);
  }

}
