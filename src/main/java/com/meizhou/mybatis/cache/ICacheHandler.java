package com.meizhou.mybatis.cache;

/**
 * Created by meizhou on 2018/8/18.
 */
public interface ICacheHandler {

    void updateKeys(CacheTableConfig cacheTableConfig, CacheSql cacheSql);

    Object getObject(CacheTableConfig cacheTableConfig, CacheSql cacheSql);

    void setObject(CacheTableConfig cacheTableConfig, CacheSql cacheSql, Object object);

}
