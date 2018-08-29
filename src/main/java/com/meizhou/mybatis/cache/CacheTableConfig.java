package com.meizhou.mybatis.cache;

import java.util.List;

/**
 * Created by meizhou on 2018/8/18.
 */
public class CacheTableConfig {

    private String prefix;

    private String tableName;

    private List<String> cacheKeys;

    private Integer expireTime = 7 * 3600 * 24;

    private Boolean isCache = true;

    private ICacheClient cacheClient;

    private ICacheHandler cacheHandler;

    private String generatedKey;

    public static CacheTableConfig build(String prefix, String tableName, List<String> cacheKeys,String generatedKey, ICacheClient cacheClient) {
        CacheTableConfig cacheTableConfig = new CacheTableConfig();
        cacheTableConfig.setPrefix(prefix);
        cacheTableConfig.setTableName(tableName);
        cacheTableConfig.setCacheKeys(cacheKeys);
        cacheTableConfig.setCacheClient(cacheClient);
        cacheTableConfig.setGeneratedKey(generatedKey);
        cacheTableConfig.setCacheHandler(new CommonCacheHandler());
        cacheTableConfig.setIsCache(true);
        cacheTableConfig.setExpireTime(7 * 3600 * 24);
        return cacheTableConfig;
    }

    public String getGeneratedKey() {
        return generatedKey;
    }

    public void setGeneratedKey(String generatedKey) {
        this.generatedKey = generatedKey;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<String> getCacheKeys() {
        return cacheKeys;
    }

    public void setCacheKeys(List<String> cacheKeys) {
        this.cacheKeys = cacheKeys;
    }

    public Integer getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Integer expireTime) {
        this.expireTime = expireTime;
    }

    public Boolean getIsCache() {
        return isCache;
    }

    public void setIsCache(Boolean isCache) {
        this.isCache = isCache;
    }

    public ICacheClient getCacheClient() {
        return cacheClient;
    }

    public void setCacheClient(ICacheClient cacheClient) {
        this.cacheClient = cacheClient;
    }

    public ICacheHandler getCacheHandler() {
        return cacheHandler;
    }

    public void setCacheHandler(ICacheHandler cacheHandler) {
        this.cacheHandler = cacheHandler;
    }
}
