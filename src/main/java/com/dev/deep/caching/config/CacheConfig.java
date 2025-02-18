package com.dev.deep.caching.config;

import com.dev.deep.caching.entity.Programmer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
public class CacheConfig {

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder
                .withCacheConfiguration("programmers",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5)));
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration(ObjectMapper ignoredJacksonObjectMapper) {
        BasicPolymorphicTypeValidator polymorphicTypeValidator =
                BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType(Programmer.class) // Allow the Programmer class.
                        .build();

        ObjectMapper objectMapper = new ObjectMapper()
                .activateDefaultTyping(polymorphicTypeValidator, ObjectMapper.DefaultTyping.NON_FINAL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(60))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)));
    }
}
