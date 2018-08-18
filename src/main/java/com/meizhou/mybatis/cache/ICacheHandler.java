package com.meizhou.mybatis.cache;

public interface ICacheHandler {

    void updateKeys(CacheConfig cacheConfig, CacheSql cacheSql);

    Object getObject(CacheConfig cacheConfig, CacheSql cacheSql);

    void setObject(CacheConfig cacheConfig, CacheSql cacheSql, Object object);
}
