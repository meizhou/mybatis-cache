package com.meizhou.mybatis.cache;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.*;

/**
 * Created by meizhou on 2018/8/18.
 */
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class CacheExecutorInterceptor implements Interceptor {

    private static Map<String, CacheConfig> cacheConfigMap = new HashMap<>();

    static {
        CacheConfig cacheConfig = new CacheConfig();
        cacheConfig.setCacheClient(new ICacheClient() {
            @Override
            public byte[] get(byte[] key) {
                System.out.println("get:" + new String(key));
                return new byte[0];
            }

            @Override
            public Boolean set(byte[] key, int exp, byte[] value) {
                System.out.println("set:" + new String(key) + ":" + exp + ":" + new String(value));
                return null;
            }
        });
        cacheConfig.setTableName("shop");
        cacheConfig.setCacheKeys(Arrays.asList("id"));
        cacheConfig.setVersion("v20");
        cacheConfig.setExpireTime(7 * 3600 * 24);
        cacheConfigMap.put("shop", cacheConfig);
    }

    private List<Object> genParameterObjectList(MappedStatement mappedStatement, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        List<Object> objectList = new ArrayList<>();
        if (parameterMappings != null) {
            for (ParameterMapping parameterMapping : parameterMappings) {
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (parameterObject == null) {
                        value = null;
                    } else if (mappedStatement.getConfiguration().getTypeHandlerRegistry().hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else {
                        MetaObject metaObject = mappedStatement.getConfiguration().newMetaObject(parameterObject);
                        value = metaObject.getValue(propertyName);
                    }
                    if (value != null) {
                        objectList.add(value);
                    }
                }
            }
        }
        return objectList;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        BoundSql boundSql = mappedStatement.getBoundSql(invocation.getArgs()[1]);
        List<Object> objectList = genParameterObjectList(mappedStatement, boundSql);
        CacheSql cacheSql = CacheSql.buildCacheSql(boundSql.getSql(), objectList);
        CacheConfig cacheConfig = cacheConfigMap.get(cacheSql.getTable());
        Object result = null;
        if (invocation.getMethod().getName().equals("query")) {
            result = CacheHandler.getObject(cacheConfig, cacheSql);
            if (result != null) {
                return result;
            }
            result = invocation.proceed();
            CacheHandler.setObject(cacheConfig, cacheSql, result);
            return result;
        }
        if (invocation.getMethod().getName().equals("update")) {
            result = invocation.proceed();
            CacheHandler.updateKeys(cacheConfig, cacheSql);
            return result;
        }
        return null;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}