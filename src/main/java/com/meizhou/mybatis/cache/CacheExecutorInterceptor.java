package com.meizhou.mybatis.cache;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by meizhou on 2018/8/18.
 */
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class CacheExecutorInterceptor implements Interceptor {

    Logger logger = LoggerFactory.getLogger(CacheExecutorInterceptor.class);

    private String dbType;

    private Boolean isCache;

    private AbstractCacheExecutorConfig abstractCacheExecutorConfig;

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
        if (!isCache) {
            return invocation.proceed();
        }
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        BoundSql boundSql = mappedStatement.getBoundSql(invocation.getArgs()[1]);
        List<Object> objectList = genParameterObjectList(mappedStatement, boundSql);
        CacheSql cacheSql = CacheSql.buildCacheSql(boundSql.getSql(), objectList, dbType);
        CacheTableConfig cacheTableConfig = abstractCacheExecutorConfig.getCacheTableConfigByTable(cacheSql.getTable());
        if (invocation.getMethod().getName().equals("query")) {
            if (isCache && cacheTableConfig != null && cacheTableConfig.getIsCache() && !CacheIgnoreThreadLocal.get()) {
                Object result = cacheTableConfig.getCacheHandler().getObject(cacheTableConfig, cacheSql);
                if (result != null) {
                    return result;
                }
            }
            Object result = invocation.proceed();
            if (isCache && cacheTableConfig != null && cacheTableConfig.getIsCache() && !CacheIgnoreThreadLocal.get()) {
                cacheTableConfig.getCacheHandler().setObject(cacheTableConfig, cacheSql, result);
            }
            return result;
        }
        if (invocation.getMethod().getName().equals("update")) {
            Object result = invocation.proceed();
            if (isCache && cacheTableConfig != null && cacheTableConfig.getIsCache()) {
                if (mappedStatement.getSqlCommandType() == SqlCommandType.INSERT && cacheTableConfig.getGeneratedKeys() != null && mappedStatement.getKeyProperties() != null && mappedStatement.getKeyProperties().length > 0) {
                    MetaObject metaObject = mappedStatement.getConfiguration().newMetaObject(boundSql.getParameterObject());
                    for (int i = 0; i < mappedStatement.getKeyProperties().length; i++) {
                        cacheSql.getParameterMap().put(cacheTableConfig.getGeneratedKeys().get(i), metaObject.getValue(mappedStatement.getKeyProperties()[i]));
                    }
                }
                cacheTableConfig.getCacheHandler().updateKeys(cacheTableConfig, cacheSql);
            }
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
        this.dbType = properties.getProperty("dbType", "mysql");
        this.isCache = Boolean.parseBoolean(properties.getProperty("isCache", "true"));
        String classType = properties.getProperty("cacheExecutorConfig");
        if (classType != null) {
            try {
                this.abstractCacheExecutorConfig = (AbstractCacheExecutorConfig) Class.forName(classType).newInstance();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}