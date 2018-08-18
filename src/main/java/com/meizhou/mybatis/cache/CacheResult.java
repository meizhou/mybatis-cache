package com.meizhou.mybatis.cache;

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
