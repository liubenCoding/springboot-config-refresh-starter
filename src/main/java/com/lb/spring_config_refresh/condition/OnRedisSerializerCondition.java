package com.lb.spring_config_refresh.condition;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.stereotype.Component;

/**
 * 不用判断了，直接给他自动装配的redisTemplate给干掉了
 */
@Component
public class OnRedisSerializerCondition implements Condition {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return redisTemplate == null || redisTemplate.getStringSerializer() instanceof JdkSerializationRedisSerializer;
    }
}
