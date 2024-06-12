package com.evgeniyfedorchenko.animalshelter.telegram.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@TestConfiguration
public class RedisTestConfiguration {

    @Bean
    @Primary
    public RedisTemplate<Long, Long> redisTemplateTest(RedisConnectionFactory connectionFactory) {
        RedisTemplate<Long, Long> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}
