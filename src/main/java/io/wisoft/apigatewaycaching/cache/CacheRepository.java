package io.wisoft.apigatewaycaching.cache;

public interface CacheRepository {

    String find(String key);

    void save(String key, String value);

    void saveWithExpireTime(String key, String value, int seconds);

    String delete(String key);

}
