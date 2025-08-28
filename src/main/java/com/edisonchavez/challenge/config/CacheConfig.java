package com.edisonchavez.challenge.config;

import com.edisonchavez.challenge.shared.Constants;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
@ConditionalOnBean(RedisConnectionFactory.class)
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory cf) {
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer();

        RedisCacheConfiguration base = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer))
                .entryTtl(Duration.ofHours(24));

        Map<String, RedisCacheConfiguration> conf = new HashMap<>();
        conf.put(Constants.CACHE_FILM_LIST,     base.entryTtl(Duration.ofHours(24)));
        conf.put(Constants.CACHE_FILM_DETAIL,   base.entryTtl(Duration.ofDays(3)));
        conf.put(Constants.CACHE_PEOPLE_LIST,    base.entryTtl(Duration.ofHours(6)));
        conf.put(Constants.CACHE_PEOPLE_DETAIL,  base.entryTtl(Duration.ofDays(3)));
        conf.put(Constants.CACHE_STARTSHIPS_LIST, base.entryTtl(Duration.ofHours(12)));
        conf.put(Constants.CACHE_STARTSHIPS_DETAIL, base.entryTtl(Duration.ofDays(3)));
        conf.put(Constants.CACHE_VEHICLE_LIST,  base.entryTtl(Duration.ofHours(12)));
        conf.put(Constants.CACHE_VEHICLE_DETAIL, base.entryTtl(Duration.ofDays(3)));

        return RedisCacheManager.builder(cf)
                .cacheDefaults(base)
                .withInitialCacheConfigurations(conf)
                .build();
    }
}