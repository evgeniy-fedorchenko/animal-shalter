package com.evgeniyfedorchenko.animalshelter.telegram.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@Configuration
@Profile("test")
public class RedisTestConfiguration {

    @Bean
    @Primary
    public RedisTemplate<String, String> redisTemplateTest(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        log.error("Test config of redisTemplate active");
        return template;
    }
}
