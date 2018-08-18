package com.meizhou.mybatis.cache;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisCacheClient implements ICacheClient {

    private JedisPool pool;

    public RedisCacheClient(String host, Integer port, String password) {
        this.pool = new JedisPool(new JedisPoolConfig(), host, Integer.valueOf(port), 1000, password);
    }

    @Override
    public byte[] get(byte[] key) {
        Jedis shardedJedis = null;
        try {
            shardedJedis = pool.getResource();
            return shardedJedis.get(key);
        } catch (Exception e) {
            return null;
        } finally {
            if (shardedJedis != null) {
                shardedJedis.close();
            }
        }
    }

    @Override
    public Boolean set(byte[] key, int exp, byte[] value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.setex(key, exp, value);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

}
