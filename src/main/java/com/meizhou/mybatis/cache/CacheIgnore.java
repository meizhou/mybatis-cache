package com.meizhou.mybatis.cache;

import java.lang.annotation.*;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CacheIgnore {

}
