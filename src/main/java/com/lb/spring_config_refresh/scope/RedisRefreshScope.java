package com.lb.spring_config_refresh.scope;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liuben
 * @date 2020/7/28 1:56 上午
 **/
@Component
public class RedisRefreshScope implements Scope {

    public static final String scopeName = "redisRefreshScope";

    private static Map<String, Object> beanCache = new HashMap<>();

    @Override
    public Object get(String beanName, ObjectFactory<?> objectFactory) {
        Object bean = beanCache.get(beanName);
        if (bean == null) {
            bean = objectFactory.getObject();
            beanCache.put(beanName, bean);
        }
        return bean;
    }

    @Override
    public Object remove(String beanName) {
        return beanCache.remove(beanName);
    }

    @Override
    public void registerDestructionCallback(String s, Runnable runnable) {

    }

    @Override
    public Object resolveContextualObject(String s) {
        return null;
    }

    @Override
    public String getConversationId() {
        return null;
    }
}
