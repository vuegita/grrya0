package com.inso.framework.redis.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.FastJsonHelper;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;
import redis.clients.jedis.ShardedJedisPool;

/**
 * nahi
 * @author Administrator
 *
 */
public class SharedJedisManager {
	
	private static Log LOG = LogFactory.getLog(SharedJedisManager.class);
	
	private static final int DEFAULT_MAX_WAIT = 10000;
	private static final int DEFAULT_MAXTOTAL = 3000;
	private static final int DEFAULT_MAXIDEL = 50;
	
	private static final SharedJedisManager instance = new SharedJedisManager();
	
	private ShardedJedisPool pool;
	
	private ShardedJedis mDefaultJedis; 
	
	public static SharedJedisManager getInstance()
	{
		return instance;
	}
	
	private SharedJedisManager()
	{
		int maxTotal = DEFAULT_MAXTOTAL;
		int maxIdle = DEFAULT_MAXIDEL;
//		int maxTotalEnv = StringUtils.asInt(System.getProperty("pika.max.total"));
//		if(maxTotalEnv > 0) maxTotal = maxTotalEnv;
		
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxWaitMillis(DEFAULT_MAX_WAIT);
		config.setMaxTotal(DEFAULT_MAXTOTAL);
		config.setMaxIdle(maxIdle);
		config.setTestOnBorrow(false);
		config.setBlockWhenExhausted(false);

		// 线程池异步
//		this.mLoadBalance = new RoundRobinSharedLoadBalance(config);
		
		MyConfiguration conf = MyConfiguration.getInstance();
		String[] hosts = conf.getStrings("pika.master.server1");
		int port = conf.getInt("pika.master.port", 9221);
		
		// master
		for(String host : hosts) {
			List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
			JedisShardInfo sharedInfo = new JedisShardInfo(host, port, 3000);
			shards.add(sharedInfo);
			this.pool = new ShardedJedisPool(config, shards);
			LOG.info("my shared client pika server = " + host);
			break;
		}
		this.mDefaultJedis = getResource();
		LOG.info("[pika-config]-[maxTotal=" + maxTotal + "]-[maxIdle=" + maxIdle + "]");
	}
	
	private ShardedJedis getResource() {
		return pool.getResource();
	}

	// ============================== get ==============================
	public String getString(String key) {
		String rs = null;
		ShardedJedis jedis = null;
		try {
			jedis = getResource();
			if(jedis != null) {
				rs = jedis.get(key);
			} else {
				rs = mDefaultJedis.get(key);
			}
		} catch (Exception e) {
			LOG.error("getString error:", e);
		} finally {
			close(jedis);
		}
		return rs;
	}
	
	public List<Object> getStringByPipeline(String... keys)
	{
		List<Object> rsList = null;
		ShardedJedis jedis = null;
		try {
			jedis = getResource();
			if(jedis == null) return null;
			
			ShardedJedisPipeline pipeline = jedis.pipelined();
			for(String key : keys)
			{
				pipeline.get(key);
			}
			rsList = pipeline.syncAndReturnAll();
		} catch (Exception e) {
		} finally {
			close(jedis);
		}
		return rsList;
	}
	
	public void setStringByPipeline(Map<String, Object> keyValue)
	{
		ShardedJedis jedis = null;
		try {
			jedis = getResource();
			if(jedis == null) return;
			ShardedJedisPipeline pipeline = jedis.pipelined();
			Set<Map.Entry<String, Object>> setMap= keyValue.entrySet();
			boolean first = true;
			boolean isString = false;
			for(Map.Entry<String, Object> entry : setMap)
			{
				if(isString)
				{
					pipeline.set(entry.getKey(), (String)entry.getValue());
				}
				else
				{
					Object value = entry.getValue();
					if(first)
					{
						if(value instanceof String)
						{
							pipeline.set(entry.getKey(), (String)value);
							isString = true;
						}
						first = false;
					}
					else
					{
						String valueString = FastJsonHelper.jsonEncode(value);
						pipeline.set(entry.getKey(), valueString);
					}
					
				}
				
			}
			pipeline.sync();
		} catch (Exception e) {
		} finally {
			close(jedis);
		}
	}

	// ============================== set ==============================
	/**
	 * <p>
	 * 设置key value,如果key已经存在则返回0,nx==> not exist
	 * </p>
	 * 
	 * @param key
	 * @param value
	 * @return 成功返回1 如果存在 和 发生异常 返回 0
	 */
	public void setString(String key, String value) {
		ShardedJedis jedis = null;
		try {
			jedis = getResource();
			if(jedis != null)
			{
				jedis.set(key, value);
			} else {
				mDefaultJedis.set(key, value);
			}
		} catch (Exception e) {
			LOG.error("setString error:", e);
		} finally {
			close(jedis);
		}
	}
	
	/**
	 * <p>
	 * 设置key value并制定这个键值的有效期
	 * </p>
	 * 
	 * @param key
	 * @param value
	 * @param seconds
	 *            单位:秒
	 * @return 成功返回OK 失败和异常返回null
	 */
	public void setString(String key, String value, int expire) {
		ShardedJedis jedis = null;
		try {
			jedis = getResource();
			if(jedis != null)
			{
				jedis.setex(key, expire, value);
			} else {
				mDefaultJedis.setex(key, expire, value);
			}
		} catch (Exception e) {
			LOG.error("setString error:", e);
		} finally {
			close(jedis);
		}
	}

	// ============================== delete ==============================
	public void delete(String key) {
		ShardedJedis jedis = null;
		try {
			jedis = getResource();
			if(jedis != null)
			{
				jedis.del(key);
			} else {
				mDefaultJedis.del(key);
			}
		} catch (Exception e) {
			LOG.error("delete error:", e);
		} finally {
			close(jedis);
		}
	}
	
	
	public boolean exists(String key)
	{
		boolean rs = false;
		ShardedJedis jedis = null;
		try {
			jedis = getResource();
			if(jedis != null)
			{
				rs = jedis.exists(key);
			} else {
				mDefaultJedis.exists(key);
			}
		} catch (Exception e) {
			LOG.error("delete error:", e);
		} finally {
			close(jedis);
		}
		return rs;
	}

	private void close(ShardedJedis redis) {
		try {
			if (redis != null)
				redis.close();
		} catch (Exception e) {
			LOG.error("close error:", e);
		}
	}
	
	public static void main(String[] args) throws IOException
	{
		
		
		
		SharedJedisManager client = SharedJedisManager.getInstance();
		client.setString("a1", "1");
		client.setString("a2", "2");
		client.setString("a3", "3");
		
		List<Object> list = client.getStringByPipeline("a1", "a2", "a3");
		
		for(Object value : list)
		{
			System.out.println(value);
		}
		
	}
	

}
