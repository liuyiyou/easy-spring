package org.springframework.beans.factory.support;

import org.springframework.beans.BeansException;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * 解析和注册BeanDefinition
 */
public interface BeanDefinitionParser {

    /**
     * 将Bean注册到BeanFactory的子类中
     *
     * @param reader   : BeanDefinitionReader  读取bean定义
     * @param doc      :  读取的地方
     * @param resource
     * @return Bean的个数
     * @throws BeansException
     */
    int registerBeanDefinitions(BeanDefinitionReader reader, Document doc, Resource resource)
            throws BeansException;

    /**
     * 加载BeanDefinition
     *
     * @param ele
     */
    void loadBeanDefinition(Element ele);

    /**
     * 解析BeanDefinition
     *
     * @param ele
     * @param beanName
     * @return
     */
    AbstractBeanDefinition parseBeanDefinition(Element ele, String beanName);


}
