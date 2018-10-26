package com.zct.quartz.springboot.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, ?> redisTemplate;
    
    /** 
     * 获取map缓存 
     * @param key 
     * @param clazz 
     * @return 
     */  
   // public  Map<String, String> getMap(String key){  
   //     BoundHashOperations<String, String,String> boundHashOperations = redisTemplate.boundHashOps(key);   
   //     return boundHashOperations.entries();  
   // }  
    public  <T> Map<String, T> getMap(String key, Class<T> clazz){  
        BoundHashOperations<String, String, T> boundHashOperations = redisTemplate.boundHashOps(key);   
        return boundHashOperations.entries();  
    }  
    
    /**
     * 
     * 获取list
     * @param key
     * @param value
     * @return
     */
    public  List<String> getList(String key){  
    	JedisPoolConfig config = new JedisPoolConfig(); // Jedis连接池
		config.setMaxIdle(8); // 最大空闲连接数
		config.setMaxTotal(8);// 最大连接数
		config.setMaxWaitMillis(1000); // 获取连接是的最大等待时间，如果超时就抛出异常
		config.setTestOnBorrow(false);// 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
		config.setTestOnReturn(true);
		JedisPool jedisPool = new JedisPool(config, "10.237.55.106", 6379); // 配置、ip、端口、连接超时时间、密码、数据库编号（0~15）
		Jedis jedis = jedisPool.getResource();
		List<String> list  = new  ArrayList<>();
		list = jedis.lrange(key, 0, -1);
		jedis.close();
		return list;
    }
    
    
    /**
     * 
     * 给redis缓存（key-value）
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, final String value) {
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                connection.set(serializer.serialize(key), serializer.serialize(value));
                return true;
            }
        });
        return result;
    }
    
    /**
     * 
     * 给redis缓存（key-value）
     * @param key
     * @param value
     * @return
     */
    public boolean getList(final String key, final String value) {
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                connection.set(serializer.serialize(key), serializer.serialize(value));
                return true;
            }
        });
        return result;
    }

    /**
     * 
     * 从redis缓存中取key对应的value
     * @param key
     * @return
     */
    public String get(final String key) {
        String result = redisTemplate.execute(new RedisCallback<String>() {

            public String doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                byte[] value = connection.get(serializer.serialize(key));
                return serializer.deserialize(value);
            }
        });
        return result;
    }

    /**
     * 
     * 给redis的key设置有效时间
     * @param key
     * @param expire
     * @return
     */
    public boolean expire(final String key, long expire) {
        return redisTemplate.expire(key, expire, TimeUnit.SECONDS);
    }

    /**
     * 在redis缓存中给key赋值List集合
     * @param key
     * @param list
     * @return
     */
    public <T> boolean setList(String key, List<T> list) {
        String value = JSONUtil.toJson(list);
        return set(key, value);
    }

    /**
     * 
     *在redis中从key取List的集合
     * @param key
     * @param clz
     * @return
     */
    public <T> List<T> getList(String key, Class<T> clz) {
        String json = get(key);
        if (json != null) {
            List<T> list = JSONUtil.toList(json, clz);
            return list;
        }
        return null;
    }

    /**
     * 
     * 在key 对应 list的头部添加字符串元素
     * @param key
     * @param obj
     * @return
     */
    public long lpush(final String key, Object obj) {
        final String value = JSONUtil.toJson(obj);
        long result = redisTemplate.execute(new RedisCallback<Long>() {

            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                long count = connection.lPush(serializer.serialize(key), serializer.serialize(value));
                return count;
            }
        });
        return result;
    }

    /**
     * 
     * 在key 对应 list 的尾部添加字符串元素
     * @param key
     * @param obj
     * @return
     */
    public long rpush(final String key, Object obj) {
        final String value = JSONUtil.toJson(obj);
        long result = redisTemplate.execute(new RedisCallback<Long>() {

            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                long count = connection.rPush(serializer.serialize(key), serializer.serialize(value));
                return count;
            }
        });
        return result;
    }

    /**
     * 
     * 用于移除并返回列表的第一个元素
     * @param key
     * @return
     */
    public String lpop(final String key) {
        String result = redisTemplate.execute(new RedisCallback<String>() {
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                byte[] res = connection.lPop(serializer.serialize(key));
                return serializer.deserialize(res);
            }
        });
        return result;
    }

    /**
     * 
     * 校验redis缓存中是否存在key
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 
     * 删除redis缓存存在key
     * @param key
     * @return
     */
    public void delete(final String key) {
        redisTemplate.delete(key);
    }
    
    /**
     * 
     * redis存list
     * @param key
     * @return
     */
    public  void setListAll(String key, List list) {
        redisTemplate.opsForList().leftPushAll(key,list);
    } 
    
    /**
     * 
     * redis取list
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
	public <T> List<T> getListAll(String key) {
        return (List<T>)redisTemplate.opsForList().range(key, 0, -1);
    } 
}
