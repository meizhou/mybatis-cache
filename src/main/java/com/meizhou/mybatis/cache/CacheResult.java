package com.meizhou.mybatis.cache;

/**
 * Created by meizhou on 2018/8/18.
 */
public class CacheResult {

    private Object object;

    public CacheResult() {
    }

    public CacheResult(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
