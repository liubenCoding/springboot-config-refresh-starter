package com.lb.spring_config_refresh.redis;

import com.lb.spring_config_refresh.refresh.RefreshConfigFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.weaving.LoadTimeWeaverAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.*;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author liuben
 * @date 2020/7/27 5:50 下午
 **/
@Component
//@ConditionalOnProperty(name = {"config.refresh.key"})
//@ConfigurationProperties(prefix = "config.refresh")
//@EnableConfigurationProperties
//@DependsOn("environment")
public class RedisConfigRefreshListener implements MessageListener, InitializingBean,LoadTimeWeaverAware {

    public RedisConfigRefreshListener() {
        System.out.println("RedisConfigRefreshListener 实例化");
        System.out.println(System.currentTimeMillis());

    }

    @Autowired
    private Environment environment;

    @Value("${config.refresh.key}")
    private String key;

    @Autowired
    private RefreshConfigFactory refreshConfigFactory;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        if (key.equals(message.toString())) {
            Map<String, Object> configMap = redisTemplate.opsForHash().entries(key);
            refreshConfigFactory.refreshConfig(configMap);
            refreshConfigFactory.resetBeanProperties();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, Object> configMap = redisTemplate.opsForHash().entries(key);
        refreshConfigFactory.refreshConfig(configMap);
    }

    @Override
    public void setLoadTimeWeaver(LoadTimeWeaver loadTimeWeaver) {
        System.out.println("loadTimeWeaver");
    }

}
