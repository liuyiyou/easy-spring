package org.springframework.beans.factory;

import org.springframework.beans.BeansException;

import java.util.Map;


public interface ListableBeanFactory extends BeanFactory {


    int getBeanDefinitionCount();


    String[] getBeanDefinitionNames();


    String[] getBeanDefinitionNames(Class type);


    boolean containsBeanDefinition(String name);


    Map getBeansOfType(Class type, boolean includePrototypes, boolean includeFactoryBeans)
            throws BeansException;

}
