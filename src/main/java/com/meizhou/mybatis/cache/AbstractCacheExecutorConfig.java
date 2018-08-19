package com.meizhou.mybatis.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by meizhou on 2018/8/18.
 */
public abstract class AbstractCacheExecutorConfig {

    Logger logger = LoggerFactory.getLogger(AbstractCacheExecutorConfig.class);

    public abstract Map<String, CacheTableConfig> getCacheTableConfigMap();

    public void init() {
        logger.info("AbstractCacheExecutorConfig==>" + getCacheTableConfigMap().keySet());
    }

    public CacheTableConfig getCacheTableConfigByTable(String tableName) {
        return getCacheTableConfigMap().get(tableName);
    }
}