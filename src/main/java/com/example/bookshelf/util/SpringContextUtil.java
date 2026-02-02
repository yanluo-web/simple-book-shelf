package com.example.bookshelf.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring 上下文持有工具类，用于获取容器中的 Bean 实例
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

    // 持有 Spring 应用上下文的静态引用
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

    /**
     * 根据 Bean 类型获取容器中的 Bean 实例（静态方法）
     */
    public static <T> T getBean(Class<T> beanClass) {
        if (applicationContext == null) {
            throw new RuntimeException("Spring 应用上下文未初始化完成");
        }
        return applicationContext.getBean(beanClass);
    }
}