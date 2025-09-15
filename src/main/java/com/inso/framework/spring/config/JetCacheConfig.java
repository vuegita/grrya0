//package com.inso.framework.spring.config;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.springframework.context.annotation.Bean;
//
//import com.alicp.jetcache.anno.CacheConsts;
//import com.alicp.jetcache.anno.support.GlobalCacheConfig;
//import com.alicp.jetcache.anno.support.SpringConfigProvider;
//import com.alicp.jetcache.embedded.EmbeddedCacheBuilder;
//import com.alicp.jetcache.embedded.LinkedHashMapCacheBuilder;
//import com.alicp.jetcache.redis.RedisCacheBuilder;
//import com.alicp.jetcache.support.FastjsonKeyConvertor;
//import com.alicp.jetcache.support.JavaValueDecoder;
//import com.alicp.jetcache.support.JavaValueEncoder;
//import com.inso.framework.redis.client.MyJedisClient;
//
//import redis.clients.jedis.JedisPool;
//
///**
// * 无法使用
// * @author Administrator
// *
// */
////@Configuration
//public class JetCacheConfig {
//	
//    @Bean
//    public SpringConfigProvider springConfigProvider() {
//        return new SpringConfigProvider();
//    }
//
//    @SuppressWarnings({ "rawtypes", "unchecked" })
//	@Bean
//    public GlobalCacheConfig config(SpringConfigProvider configProvider){
//    	
//    	JedisPool pool = MyJedisClient.getInstanced().getPool();
//    	
//        Map localBuilders = new HashMap();
//        EmbeddedCacheBuilder localBuilder = LinkedHashMapCacheBuilder
//                .createLinkedHashMapCacheBuilder()
//                .keyConvertor(FastjsonKeyConvertor.INSTANCE);
//        localBuilders.put(CacheConsts.DEFAULT_AREA, localBuilder);
//
//        Map remoteBuilders = new HashMap();
//        RedisCacheBuilder remoteCacheBuilder = RedisCacheBuilder.createRedisCacheBuilder()
//                .keyConvertor(FastjsonKeyConvertor.INSTANCE)
//                .valueEncoder(JavaValueEncoder.INSTANCE)
//                .valueDecoder(JavaValueDecoder.INSTANCE)
//                // 默认默认缓存
//                .jedisPool(pool);
//        
//        remoteBuilders.put(CacheConsts.DEFAULT_AREA, remoteCacheBuilder);
//
//        GlobalCacheConfig globalCacheConfig = new GlobalCacheConfig();
//        globalCacheConfig.setConfigProvider(configProvider);
//        globalCacheConfig.setLocalCacheBuilders(localBuilders);
//        globalCacheConfig.setRemoteCacheBuilders(remoteBuilders);
//        globalCacheConfig.setStatIntervalMinutes(0); // 间隔时间，0表示不统计
//        globalCacheConfig.setAreaInCacheName(false);
//
//        return globalCacheConfig;
//    }
//
//}
