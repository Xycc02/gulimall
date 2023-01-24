package com.xuyuchao.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @Description: RedisonConfig
 * @Author XYC
 * @Date: 2023/1/11 22:00
 * @Version 1.0
 */
@Configuration
public class RedissonConfig {
    @Bean(destroyMethod="shutdown")
    RedissonClient redisson() throws IOException {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.174.103:6379");
        return Redisson.create(config);
    }
}
