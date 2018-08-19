package com.meizhou.mybatis.cache;

import java.util.List;

/**
 * Created by meizhou on 2018/8/18.
 */
public class CacheConfig {

    private List<String> cacheKeys;

    private String tableName;

    private Boolean isSharding;

    private int expireTime;

    private boolean isCache;

    private String prefix;

    private ICacheClient cacheClient;

    private ICacheHandler cacheHandler;

    public ICacheHandler getCacheHandler() {
        return cacheHandler;
    }

    public void setCacheHandler(ICacheHandler cacheHandler) {
        this.cacheHandler = cacheHandler;
    }

    public List<String> getCacheKeys() {
        return cacheKeys;
    }

    public void setCacheKeys(List<String> cacheKeys) {
        this.cacheKeys = cacheKeys;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Boolean getIsSharding() {
        return isSharding;
    }

    public void setIsSharding(Boolean isSharding) {
        this.isSharding = isSharding;
    }

    public int getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(int expireTime) {
        this.expireTime = expireTime;
    }

    public boolean getIsCache() {
        return isCache;
    }

    public void setIsCache(boolean isCache) {
        this.isCache = isCache;
    }

    public ICacheClient getCacheClient() {
        return cacheClient;
    }

    public void setCacheClient(ICacheClient cacheClient) {
        this.cacheClient = cacheClient;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
