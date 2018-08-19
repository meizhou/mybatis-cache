package com.meizhou.mybatis.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by meizhou on 2018/8/18.
 */
public class RedisCacheClient implements ICacheClient {

    Logger logger = LoggerFactory.getLogger(RedisCacheClient.class);

    private JedisPool pool;

    public RedisCacheClient(String host, Integer port, String password) {
        this.pool = new JedisPool(new JedisPoolConfig(), host, Integer.valueOf(port), 1000, password);
    }

    @Override
    public byte[] get(byte[] key) {
        if (logger.isDebugEnabled()) {
            logger.debug("RedisCacheClient get==>" + new String(key));
        }
        Jedis shardedJedis = null;
        try {
            shardedJedis = pool.getResource();
            return shardedJedis.get(key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        } finally {
            if (shardedJedis != null) {
                shardedJedis.close();
            }
        }
    }

    @Override
    public Boolean set(byte[] key, int exp, byte[] value) {
        if (logger.isDebugEnabled()) {
            logger.debug("RedisCacheClient set==>" + new String(key));
        }
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.setex(key, exp, value);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

}
