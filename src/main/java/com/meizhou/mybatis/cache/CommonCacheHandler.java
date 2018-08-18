package com.meizhou.mybatis.cache;

import java.security.MessageDigest;

public class CommonCacheHandler implements ICacheHandler {

    @Override
    public void updateKeys(CacheConfig cacheConfig, CacheSql cacheSql) {
        if (cacheConfig == null) {
            return;
        }
        for (String cacheKey : cacheConfig.getCacheKeys()) {
            Object value = cacheSql.getParameterMap().get(cacheKey);
            String key = cacheConfig.getVersion() + "." + cacheSql.getTable() + "." + cacheKey + ":" + value;
            cacheConfig.getCacheClient().set(key.getBytes(), 30 * 3600 * 24, (System.currentTimeMillis() + "").getBytes());
        }
    }

    @Override
    public Object getObject(CacheConfig cacheConfig, CacheSql cacheSql) {
        if (cacheConfig == null) {
            return null;
        }
        for (String cacheKey : cacheConfig.getCacheKeys()) {
            Object value = cacheSql.getParameterMap().get(cacheKey);
            if (value != null) {
                String key = cacheConfig.getVersion() + "." + cacheSql.getTable() + "." + cacheKey + ":" + value;
                byte[] version = cacheConfig.getCacheClient().get(key.getBytes());
                if (version != null && version.length > 0) {
                    byte[] bytes = cacheConfig.getCacheClient().get((key + ":" + new String(version) + ":" + md5Encoding(cacheSql.getSql())).getBytes());
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
    public void setObject(CacheConfig cacheConfig, CacheSql cacheSql, Object object) {
        if (cacheConfig == null) {
            return;
        }
        for (String cacheKey : cacheConfig.getCacheKeys()) {
            Object value = cacheSql.getParameterMap().get(cacheKey);
            if (value != null) {
                String key = cacheConfig.getVersion() + "." + cacheSql.getTable() + "." + cacheKey + ":" + value;
                byte[] version = cacheConfig.getCacheClient().get(key.getBytes());
                if (version == null || version.length == 0) {
                    version = (System.currentTimeMillis() + "").getBytes();
                    cacheConfig.getCacheClient().set(key.getBytes(), 30 * 3600 * 24, version);
                }
                cacheConfig.getCacheClient().set((key + ":" + new String(version) + ":" + md5Encoding(cacheSql.getSql())).getBytes(), cacheConfig.getExpireTime(), ProtostuffUtils.serialize(new CacheResult(object)));
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
