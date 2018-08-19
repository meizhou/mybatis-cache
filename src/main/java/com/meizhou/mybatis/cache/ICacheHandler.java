package com.meizhou.mybatis.cache;

/**
 * Created by meizhou on 2018/8/18.
 */
public interface ICacheHandler {

    void updateKeys(CacheConfig cacheConfig, CacheSql cacheSql);

    Object getObject(CacheConfig cacheConfig, CacheSql cacheSql);

    void setObject(CacheConfig cacheConfig, CacheSql cacheSql, Object object);



}
