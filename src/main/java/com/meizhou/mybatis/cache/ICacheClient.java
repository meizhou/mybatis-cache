package com.meizhou.mybatis.cache;

public interface ICacheClient {

    byte[] get(byte[] key);

    Boolean set(byte[] key, int exp, byte[] value);

}
