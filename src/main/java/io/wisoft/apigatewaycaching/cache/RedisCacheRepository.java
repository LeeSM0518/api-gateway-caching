package io.wisoft.apigatewaycaching.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.List;

import static java.lang.Long.MAX_VALUE;

@Repository
public class RedisCacheRepository implements CacheRepository {

  private static final String COMMAND_CACHE_KEY = "COMMAND:";
  private static final String QUERY_CACHE_KEY = "QUERY:";

  private final ValueOperations<String, String> valueOperations;
  private final ListOperations<String, String> listOperations;

  @Autowired
  public RedisCacheRepository(final StringRedisTemplate stringTemplate) {
    this.valueOperations = stringTemplate.opsForValue();
    this.listOperations = stringTemplate.opsForList();
  }

  @Override
  public String findQueryCache(final String key) {
    return valueOperations.get(QUERY_CACHE_KEY + key);
  }

  @Override
  public List<String> findCommandCache(String key) {
    return listOperations.range(COMMAND_CACHE_KEY + key, 0, MAX_VALUE);
  }

  @Override
  public void saveCommandCache(String key, String value) {
    listOperations.rightPush(COMMAND_CACHE_KEY + key, value);
  }

  @Override
  public void saveQueryCache(String key, String value) {
    valueOperations.set(QUERY_CACHE_KEY + key, value);
  }

  @Override
  public String deleteQueryCache(final String key) {
    return valueOperations.getAndDelete(QUERY_CACHE_KEY + key);
  }

  @Override
  public Boolean clearCommandCache(String key) {
    return listOperations.getOperations().delete(COMMAND_CACHE_KEY + key);
  }
}
