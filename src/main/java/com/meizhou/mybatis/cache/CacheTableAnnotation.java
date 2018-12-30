package com.meizhou.mybatis.cache;

import java.lang.annotation.*;

/**
 * Created by meizhou on 2018/12/27.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CacheTableAnnotation {

    String prefix();

    String tableName();

    String[] cacheKeys();

    int expireTime() default 7 * 3600 * 24;

    boolean isCache() default true;

    String cacheClient();

    String cacheHandler() default "CommonCacheHandler";

    String[] generatedKeys();

}
