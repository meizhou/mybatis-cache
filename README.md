# mybatis-cache
一个基于mybatis插件的缓存，可以精确到具体的索引key

```
<bean id="cacheIgnoreInterceptor" class="com.meizhou.mybatis.cache.CacheIgnoreInterceptor"></bean>
```

```
public class FlyCacheExecutorConfig extends AbstractCacheExecutorConfig {

    private Map<String, CacheTableConfig> cacheTableConfigMap = Maps.newConcurrentMap();

    public FlyCacheExecutorConfig() {
        ICacheClient cacheClient = new RedisCacheClient("localhost", 6379, "123456");
        CacheTableConfig cacheTableConfig = CacheTableConfig.build("v20", "shop", Lists.newArrayList("id"), cacheClient);
        cacheTableConfigMap.put("shop", cacheTableConfig);
        for (int i = 0; i < 1024; i++) {
            cacheTableConfigMap.put("shop_" + i, cacheTableConfig);
        }
    }

    @Override
    public Map<String, CacheTableConfig> getCacheTableConfigMap() {
        return cacheTableConfigMap;
    }
}
```

```
<plugin interceptor="com.meizhou.mybatis.cache.CacheExecutorInterceptor">
    <property name="dbType" value="mysql"/>
    <property name="isCache" value="true"/>
    <property name="cacheExecutorConfig" value="com.meizhou.fly.server.constant.FlyCacheExecutorConfig"/>
</plugin>
```
