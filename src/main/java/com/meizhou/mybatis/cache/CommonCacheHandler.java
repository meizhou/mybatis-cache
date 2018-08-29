package com.meizhou.mybatis.cache;

import java.security.MessageDigest;

/**
 * Created by meizhou on 2018/8/18.
 */
public class CommonCacheHandler implements ICacheHandler {

    @Override
    public void updateKeys(CacheTableConfig cacheTableConfig, CacheSql cacheSql) {
        if (cacheTableConfig == null) {
            return;
        }
        for (String cacheKey : cacheTableConfig.getCacheKeys()) {
            Object value = cacheSql.getParameterMap().get(cacheKey);
            if (value == null) {
                if (cacheSql.getIsInsert() && cacheTableConfig.getGeneratedKeys() == null) {
                    continue;
                }
                throw new RuntimeException(("cache key:" + cacheKey + " must value and sql is:" + cacheSql + " config:" + cacheTableConfig));
            }
            String versionKey = cacheTableConfig.getPrefix() + "." + cacheSql.getTable() + "." + cacheKey + ":" + value;
            cacheTableConfig.getCacheClient().set(versionKey.getBytes(), 15 * 3600 * 24, (System.currentTimeMillis() + "").getBytes());
        }
    }

    @Override
    public Object getObject(CacheTableConfig cacheTableConfig, CacheSql cacheSql) {
        if (cacheTableConfig == null) {
            return null;
        }
        for (String cacheKey : cacheTableConfig.getCacheKeys()) {
            Object value = cacheSql.getParameterMap().get(cacheKey);
            if (value != null) {
                String versionKey = cacheTableConfig.getPrefix() + "." + cacheSql.getTable() + "." + cacheKey + ":" + value;
                byte[] version = cacheTableConfig.getCacheClient().get(versionKey.getBytes());
                if (version != null && version.length > 0) {
                    byte[] bytes = cacheTableConfig.getCacheClient().get((versionKey + ":" + new String(version) + "." + md5Encoding(cacheSql.getSql())).getBytes());
                    if (bytes != null && bytes.length > 0) {
                        CacheResult response = ProtostuffUtils.deserialize(bytes, CacheResult.class);
                        return response.getObject();
                    }
                }
                break;
            }
        }
        return null;
    }


    @Override
    public void setObject(CacheTableConfig cacheTableConfig, CacheSql cacheSql, Object object) {
        if (cacheTableConfig == null) {
            return;
        }
        for (String cacheKey : cacheTableConfig.getCacheKeys()) {
            Object value = cacheSql.getParameterMap().get(cacheKey);
            if (value != null) {
                String versionKey = cacheTableConfig.getPrefix() + "." + cacheSql.getTable() + "." + cacheKey + ":" + value;
                byte[] version = cacheTableConfig.getCacheClient().get(versionKey.getBytes());
                if (version == null || version.length == 0) {
                    version = (System.currentTimeMillis() + "").getBytes();
                    cacheTableConfig.getCacheClient().set(versionKey.getBytes(), 15 * 3600 * 24, version);
                }
                cacheTableConfig.getCacheClient().set((versionKey + ":" + new String(version) + "." + md5Encoding(cacheSql.getSql())).getBytes(), cacheTableConfig.getExpireTime(), ProtostuffUtils.serialize(new CacheResult(object)));
                break;
            }
        }
    }

    private static String md5Encoding(String s) {
        try {
            byte[] e = s.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("md5");
            mdInst.update(e);
            byte[] md = mdInst.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; ++i) {
                byte byte0 = md[i];
                str[k++] = "0123456789abcdef".charAt(byte0 >>> 4 & 15);
                str[k++] = "0123456789abcdef".charAt(byte0 & 15);
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }
}
