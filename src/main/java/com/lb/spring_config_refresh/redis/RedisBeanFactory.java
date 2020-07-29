package com.lb.spring_config_refresh.redis;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.lb.spring_config_refresh.condition.RedisSerializerCondition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liuben
 * @date 2020/7/27 5:32 下午
 **/
@Configuration
public class RedisBeanFactory {

    @Value("${config.refresh.key}")
    private String configKey;


    @Value("${spring.redis.database}")
    private String database;

    @Bean
    @RedisSerializerCondition
    public RedisSerializer fastJsonRedisSerializer() {
        return new FastJsonRedisSerializer(Object.class);
    }

    @Bean
    @RedisSerializerCondition
    public RedisTemplate redisTemplate(RedisConnectionFactory factory, RedisSerializer fastJsonRedisSerializer) {
        System.out.println("创建了fastjson方式序列化的redisTemplate");
        RedisTemplate redisTemplate = new RedisTemplate<>();
        RedisSerializer stringSerializer = new StringRedisSerializer();//序列化为String
        redisTemplate.setHashValueSerializer(fastJsonRedisSerializer);
        redisTemplate.setHashKeySerializer(fastJsonRedisSerializer);
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setConnectionFactory(factory);
        redisTemplate.setValueSerializer(fastJsonRedisSerializer);
        return redisTemplate;
    }

    @Bean
    public RedisConfigRefreshListener redisKeyListener() {
        return new RedisConfigRefreshListener();
    }

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        List<Topic> topics = new ArrayList<>();
        //监听0号库和1号库中的string 键的相关操作
        System.out.println(configKey);
        topics.add(new ChannelTopic(configKey));
        topics.add(new ChannelTopic("__keyevent@" + database + "__:hset"));
        container.addMessageListener(redisKeyListener(), topics);
        return container;
    }

}
