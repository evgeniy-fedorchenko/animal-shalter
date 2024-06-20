package com.evgeniyfedorchenko.animalshelter.telegram.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Configuration
@Profile("!test")
public class RedisConfiguration implements ApplicationListener<ContextClosedEvent> {

    private final Environment environment;
    private RedisTemplate<String, String> redisTemplate;

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        log.info("No-test config of redisTemplate active");

        redisTemplate = template;
        return template;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        if (
                environment.getActiveProfiles().length > 0
                        && environment.getActiveProfiles()[0].equals("dev")
                        && redisTemplate != null
        ) {

            Set<String> keys = new HashSet<>();
            try (Cursor<String> cursor = redisTemplate.scan(ScanOptions.scanOptions().match("[-\\d]").build())) {

                while (cursor.hasNext()) {
                    keys.add(cursor.next());
                }

                redisTemplate.delete(keys);
            }

            log.info("Existing Redis's keys are dropped. Shutdown");
        }
    }
}
