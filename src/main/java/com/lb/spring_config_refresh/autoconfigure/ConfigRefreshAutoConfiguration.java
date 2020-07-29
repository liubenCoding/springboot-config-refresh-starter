package com.lb.spring_config_refresh.autoconfigure;

import com.lb.spring_config_refresh.condition.LoadRefreshCondition;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author liuben
 * @date 2020/7/28 5:37 下午
 **/
@Configuration
//@ConditionalOnProperty("config.refresh.key")
//不知道为啥没用，我还是自己写吧
//@ConditionalOnExpression("${config.refresh.enable} == true")
@LoadRefreshCondition
@ComponentScan("com.lb.spring_config_refresh")
public class ConfigRefreshAutoConfiguration {
    public ConfigRefreshAutoConfiguration() {
        System.out.println("ConfigRefreshAutoConfiguration init()");
    }
}
