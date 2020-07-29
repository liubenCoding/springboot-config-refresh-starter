package com.lb.spring_config_refresh.condition;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * 判断是否需要生成fastjson的redisTemplate
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnLoadRefreshCondition.class)
public @interface LoadRefreshCondition {
}
