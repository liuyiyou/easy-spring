
package org.springframework.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;


public interface ConfigurableApplicationContext extends ApplicationContext {


    void setParent(ApplicationContext parent);


    void addBeanFactoryPostProcessor(BeanFactoryPostProcessor beanFactoryPostProcessor);


    /**
     * 重要接口
     *
     * @throws BeansException
     */
    void refresh() throws BeansException;


    ConfigurableListableBeanFactory getBeanFactory();


    void close() throws ApplicationContextException;

}
