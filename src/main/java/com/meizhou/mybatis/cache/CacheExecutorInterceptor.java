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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by meizhou on 2018/8/18.
 */
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class CacheExecutorInterceptor implements Interceptor {

    Logger logger = LoggerFactory.getLogger(CacheExecutorInterceptor.class);

    private String dbType;

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
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        BoundSql boundSql = mappedStatement.getBoundSql(invocation.getArgs()[1]);
        List<Object> objectList = genParameterObjectList(mappedStatement, boundSql);
        CacheSql cacheSql = CacheSql.buildCacheSql(boundSql.getSql(), objectList, dbType);
        CacheTableConfig cacheTableConfig = abstractCacheExecutorConfig.getCacheTableConfigByTable(cacheSql.getTable());
        if (invocation.getMethod().getName().equals("query")) {
            if (cacheTableConfig != null && cacheTableConfig.getIsCache() && !CacheIgnoreThreadLocal.get()) {
                Object result = cacheTableConfig.getCacheHandler().getObject(cacheTableConfig, cacheSql);
                if (result != null) {
                    return result;
                }
            }
            Object result = invocation.proceed();
            if (cacheTableConfig != null && cacheTableConfig.getIsCache() && !CacheIgnoreThreadLocal.get()) {
                cacheTableConfig.getCacheHandler().setObject(cacheTableConfig, cacheSql, result);
            }
            return result;
        }
        if (invocation.getMethod().getName().equals("update")) {
            Object result = invocation.proceed();
            if (cacheTableConfig != null && cacheTableConfig.getIsCache()) {
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
        String classType = properties.getProperty("cacheExecutorConfig");
        try {
            this.abstractCacheExecutorConfig = (AbstractCacheExecutorConfig) Class.forName(classType).newInstance();
            this.abstractCacheExecutorConfig.init();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}