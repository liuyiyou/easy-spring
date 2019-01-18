package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;


public interface BeanFactoryPostProcessor {


    /**
     * 在标准初始化之后修改应用程序上下文的内部bean工厂。 将加载所有bean定义，但尚未实例化任何bean。
     * 这允许覆盖或添加属性，甚至是初始化bean。
     *
     * @param beanFactory
     * @throws BeansException
     */
    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;

}
