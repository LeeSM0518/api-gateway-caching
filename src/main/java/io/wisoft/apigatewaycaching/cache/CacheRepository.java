package io.wisoft.apigatewaycaching.cache;

import java.net.ResponseCache;

public interface CacheRepository {

  String find(String key);
  void save(String key, String value);

  String delete(String key);

}
