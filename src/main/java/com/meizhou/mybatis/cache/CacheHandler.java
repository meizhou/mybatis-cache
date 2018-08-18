package com.meizhou.mybatis.cache;

import java.security.MessageDigest;

public class CacheHandler {

    public static void updateKeys(CacheConfig cacheConfig, CacheSql cacheSql) {
        if (cacheConfig == null) {
            return;
        }
        for (String cacheKey : cacheConfig.getCacheKeys()) {
            Object value = cacheSql.getParameterMap().get(cacheKey);
            String key = cacheConfig.getVersion() + "." + cacheSql.getTable() + "." + cacheKey + ":" + value;
            cacheConfig.getCacheClient().set(key.getBytes(), 0, (System.currentTimeMillis() + "").getBytes());
        }
    }

    public static Object getObject(CacheConfig cacheConfig, CacheSql cacheSql) {
        if (cacheConfig == null) {
            return null;
        }
        for (String cacheKey : cacheConfig.getCacheKeys()) {
            Object value = cacheSql.getParameterMap().get(cacheKey);
            if (value != null) {
                String key = cacheConfig.getVersion() + "." + cacheSql.getTable() + "." + cacheKey + ":" + value;
                String version = new String(cacheConfig.getCacheClient().get(key.getBytes()));
                if (version.trim().length() > 0) {
                    byte[] bytes = cacheConfig.getCacheClient().get((key + ":" + version).getBytes());
                    if (bytes.length > 0) {
                        return ProtostuffUtils.deserialize(bytes, Object.class);
                    }
                }
                break;
            }
        }
        return null;
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
        } catch (Exception var9) {
            return null;
        }
    }

    public static Object setObject(CacheConfig cacheConfig, CacheSql cacheSql, Object object) {
        if (cacheConfig == null) {
            return null;
        }
        for (String cacheKey : cacheConfig.getCacheKeys()) {
            Object value = cacheSql.getParameterMap().get(cacheKey);
            if (value != null) {
                String key = cacheConfig.getVersion() + "." + cacheSql.getTable() + "." + cacheKey + ":" + value;
                String version = new String(cacheConfig.getCacheClient().get(key.getBytes()));
                if (version.trim().length() > 0) {
                    version = System.currentTimeMillis() + "";
                    cacheConfig.getCacheClient().set(key.getBytes(), 0, version.getBytes());
                }
                cacheConfig.getCacheClient().set((key + ":" + version + ":" + md5Encoding(cacheSql.getSql())).getBytes(), cacheConfig.getExpireTime(), ProtostuffUtils.serialize(object));
            }
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(new String(new byte[0]).length());
    }
}
