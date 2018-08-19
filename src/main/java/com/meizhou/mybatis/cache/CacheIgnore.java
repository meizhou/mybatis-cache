package com.meizhou.mybatis.cache;

import java.lang.annotation.*;

/**
 * Created by meizhou on 2018/8/18.
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CacheIgnore {

}
