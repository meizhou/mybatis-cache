package com.meizhou.mybatis.cache;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * Created by meizhou on 2018/08/19
 */
@Aspect
public class CacheIgnoreInterceptor {

    @Around("@annotation(com.meizhou.mybatis.cache.CacheIgnore)")
    public Object printServiceLog(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            CacheIgnoreThreadLocal.add();
            return joinPoint.proceed();
        } finally {
            CacheIgnoreThreadLocal.pop();
        }
    }

}
