package com.meizhou.mybatis.cache;

import java.util.Map;

/**
 * Created by meizhou on 2018/8/18.
 */
public abstract class AbstractCacheExecutorConfig {

    public abstract Map<String, CacheTableConfig> getCacheTableConfigMap();

    public CacheTableConfig getCacheTableConfigByTable(String tableName) {
        return getCacheTableConfigMap().get(tableName);
    }
}