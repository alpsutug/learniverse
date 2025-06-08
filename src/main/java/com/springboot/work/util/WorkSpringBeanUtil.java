package com.springboot.work.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Component
public class WorkSpringBeanUtil implements ApplicationContextAware {
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static <T> T getBean(Class<T> beanClass) {
        return Objects.nonNull(context) ? context.getBean(beanClass) : null;
    }

    public static <T> T getBean(Class<T> beanClass, String beanName) {
        return Objects.nonNull(context) && Objects.nonNull(beanName) ? context.getBean(beanName, beanClass) : null;
    }

    public static <T> T getProperty(String propertyName, Class<T> propertyClass) {
        return Objects.nonNull(context) && StringUtils.hasText(propertyName) ? context.getEnvironment().getProperty(propertyName, propertyClass) : null;
    }

}
