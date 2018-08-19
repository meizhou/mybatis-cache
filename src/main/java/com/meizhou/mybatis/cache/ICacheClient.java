package com.meizhou.mybatis.cache;

/**
 * Created by meizhou on 2018/8/18.
 */
public interface ICacheClient {

    byte[] get(byte[] key);

    Boolean set(byte[] key, int exp, byte[] value);

}
