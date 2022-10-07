package io.wisoft.apigatewaycaching.cache;

import java.util.List;

public interface CacheRepository {

  String findQueryCache(String key);

  List<String> findCommandCache(String key);

  void saveCommandCache(String key, String values);

  void saveQueryCache(String key, String value);

  String deleteQueryCache(String key);

  Boolean clearCommandCache(String key);

}
