package com.lb.spring_config_refresh.refresh;

import com.alibaba.druid.pool.DruidDataSource;

import com.lb.spring_config_refresh.scope.RedisRefreshScopeRegistry;
import com.lb.spring_config_refresh.utils.AnnotationSeakUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationBeanFactoryMetadata;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.*;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import java.util.List;
import java.util.Map;

/**
 * 刷新config工厂
 *
 * @author liuben
 * @date 2020/7/28 12:05 上午
 **/
@Component
//@ConditionalOnProperty(name = {"config.refresh.key"})
public class RefreshConfigFactory {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RedisRefreshScopeRegistry redisRefreshScopeRegistry;

    @Autowired
    private  AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor;

    @Autowired
    public  ConfigurationPropertiesBindingPostProcessor configurationPropertiesBindingPostProcessor;

    private static Map<String, Object> configMap = null;

    @Value("${config.refresh.key}")
    private static String REDIS_CONFIG_NAME = "redisConfig";

    //刷新配置
    public void refreshConfig(Map<String, Object> configMap) {
        if (this.configMap == null || configMap != null && !configMap.equals(this.configMap)) {
            this.configMap = configMap;
            refreshPropertySource();
        }
    }

    public void resetBeanProperties() {
        try {
            String[] beanDefinitionNames = redisRefreshScopeRegistry.getRegistry().getBeanDefinitionNames();
            for (String beanDefinitionName : beanDefinitionNames) {
                Object bean = redisRefreshScopeRegistry.getBeanFactory().getBean(beanDefinitionName);
                if (AnnotationSeakUtils.hasConfigurationPropertiesAnnotation(bean, beanDefinitionName)) {
                    //用于类上有@ConfigurationProperties注解或者@Bean方式创建有此注解的bean属性重新复制
                    configurationPropertiesBindingPostProcessor.postProcessBeforeInitialization(bean, beanDefinitionName);
                    //数据源的话。需要重启
                    if (druidIsImported() && bean instanceof DruidDataSource) {
                        ((DruidDataSource) bean).restart();
                    }
                } else if (AnnotationSeakUtils.hasValueAnnotation(bean.getClass())) {
                    //用于bean的属性,从配置信息重新赋值
                    autowiredAnnotationBeanPostProcessor.postProcessPropertyValues(null, null, bean, beanDefinitionName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("刷新配置完毕");
        }
    }

    private void refreshPropertySource() {
        Environment environment = applicationContext.getEnvironment();
        Class environmentClass = AbstractEnvironment.class;
        try {
            Field propertySourcesField = environmentClass.getDeclaredField("propertySources");
            ReflectionUtils.makeAccessible(propertySourcesField);
            MutablePropertySources mutablePropertySources = (MutablePropertySources) propertySourcesField.get(environment);

            Field propertySourceListField = MutablePropertySources.class.getDeclaredField("propertySourceList");
            ReflectionUtils.makeAccessible(propertySourceListField);
            List<PropertySource> propertySources = (List<PropertySource>) ReflectionUtils.getField(propertySourceListField, mutablePropertySources);

            Boolean hasResetPropertySource = false;

            for (PropertySource propertySource : propertySources) {
                if (propertySource.getName().equals(REDIS_CONFIG_NAME)) {
                    Field sourceField = PropertySource.class.getDeclaredField("source");
                    ReflectionUtils.makeAccessible(sourceField);
                    sourceField.set(propertySource, configMap);
                    hasResetPropertySource = true;
                }
            }
            //从来没创建过redis的propertySource
            if (!hasResetPropertySource) {
                propertySources.add(new MapPropertySource(REDIS_CONFIG_NAME, configMap));
            }
            propertySourceListField.set(mutablePropertySources, propertySources);
            propertySourcesField.set(environment, mutablePropertySources);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 感觉没啥用,已经强依赖Druid数据源这个包了
     *
     * @return
     */
    private boolean druidIsImported() {
        try {
            Class.forName("com.alibaba.druid.pool.DruidDataSource");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }
}
