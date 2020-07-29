package com.lb.spring_config_refresh.utils;

import com.lb.spring_config_refresh.refresh.RefreshConfigFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationBeanFactoryMetadata;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liuben
 * @date 2020/7/29 7:40 下午
 **/
@Component
public class AnnotationSeakUtils implements InitializingBean {

    @Autowired
    private  ConfigurationPropertiesBindingPostProcessor configurationPropertiesBindingPostProcessor;

    private static Field beanFactoryMetadataField = null;

    private static ConfigurationBeanFactoryMetadata factoryMetadata = null;

    public static Map<Class, Class> beanConfigurationPropertiesAnnotationMap = new ConcurrentHashMap<>();

    public static Map<Class, Class> beanValueAnnotationMap = new HashMap<>();

    public static boolean hasConfigurationPropertiesAnnotation(Object bean, String beanName) {
        if (beanConfigurationPropertiesAnnotationMap.containsKey(bean.getClass())) {
            return beanConfigurationPropertiesAnnotationMap.get(bean.getClass()) != null;
        } else {
            Annotation annotation = factoryMetadata.findFactoryAnnotation(beanName, ConfigurationProperties.class);
            if (annotation == null) {
                annotation = org.springframework.core.annotation.AnnotationUtils.findAnnotation(bean.getClass(), ConfigurationProperties.class);
            }
            beanValueAnnotationMap.put(bean.getClass(), annotation != null ? ConfigurationProperties.class : null);
            return annotation != null;
        }
    }

    public static boolean hasValueAnnotation(Class beanClassType) {
        if (beanValueAnnotationMap.containsKey(beanClassType)) {
            return beanValueAnnotationMap.get(beanClassType) != null;
        } else {
            Field[] fields = beanClassType.getDeclaredFields();
            for (Field field : fields) {
                if (field.getDeclaredAnnotation(Value.class) != null) {
                    beanValueAnnotationMap.put(beanClassType, Value.class);
                    return true;
                } else {
                    beanValueAnnotationMap.put(beanClassType, null);
                }
            }
        }
        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        beanFactoryMetadataField = ConfigurationPropertiesBindingPostProcessor.class.getDeclaredField("beanFactoryMetadata");
        ReflectionUtils.makeAccessible(beanFactoryMetadataField);
        factoryMetadata = (ConfigurationBeanFactoryMetadata) beanFactoryMetadataField.get(configurationPropertiesBindingPostProcessor);
    }
}
