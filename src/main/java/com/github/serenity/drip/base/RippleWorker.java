package com.github.serenity.drip.base;

import com.github.serenity.drip.support.RsaHelper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 生成8位兑换码
 * 1位预留标识 + 7位随机序列
 */
public class RippleWorker {

    private static final int MAX_WORKING_TIME = 86400;
    private static final int MAX_WORKING_SIZE = 100000;
    private static final String KEY_WORKER = "id_ripple_worker";
    private static final String PREFIX = "1";

    private JedisPool jedisPool;

    private RsaHelper rsaHelper;

    public RippleWorker() {
        jedisPool = new JedisPool();
        rsaHelper = new RsaHelper(1192, 967L, 9999999L);
    }


    public String nextId() {
        long codec = rsaHelper.codec(generate(KEY_WORKER));
        while (codec <= MAX_WORKING_SIZE) {
            codec = rsaHelper.codec(generate(KEY_WORKER));
        }
        return PREFIX + String.format("%07d", codec);
    }

    public String decodeId(String id) {
        long decode = rsaHelper.decode(Long.parseLong(id.substring(1)));
        return PREFIX + String.format("%07d", decode);
    }


    protected long generate(String key) {
        return generate(key, MAX_WORKING_TIME);
    }

    protected long generate(String key, int expire) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            long value = jedis.incr(key);
            if(expire >= 0) jedis.expire(key, expire);
            return value;
        }
        finally {
            release(jedis);
        }
    }

    private void release(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

}
