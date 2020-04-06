package com.cloudberry.cloudberry.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

@Component
public class LoggingInjector implements BeanPostProcessor {

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object postProcessBeforeInitialization(final Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            ReflectionUtils.makeAccessible(field);
            if (field.getAnnotation(Logging.class) != null) {
                Logger log = LoggerFactory.getLogger(bean.getClass());
                field.set(bean, log);
            }
        });
        return bean;
    }
}
