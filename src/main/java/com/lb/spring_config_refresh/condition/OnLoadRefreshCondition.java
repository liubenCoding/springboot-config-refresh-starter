package com.lb.spring_config_refresh.condition;


import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Component;

@Component
public class OnLoadRefreshCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment environment = context.getEnvironment();
        String configRefreshEnabled = environment.getProperty("config.refresh.enable");
        String configKey = environment.getProperty("config.refresh.key");
        if (configRefreshEnabled != null && configRefreshEnabled.equals("true")
                && configKey != null) {
            return true;
        }
        return false;
    }
}
