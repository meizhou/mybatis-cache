# mybatis-cache
一个基于mybatis插件的缓存，可以精确到具体的索引key

```
<bean id="cacheIgnoreInterceptor" class="com.meizhou.mybatis.cache.CacheIgnoreInterceptor"></bean>
```

```
<plugin interceptor="com.meizhou.mybatis.cache.CacheExecutorInterceptor">
</plugin>
```
