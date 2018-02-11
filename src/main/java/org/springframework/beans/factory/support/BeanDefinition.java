package org.springframework.beans.factory.support;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.ConstructorArgumentValues;

/**
 * 这只是一个最小的接口
 * 主要目的是允许BeanFactoryPostProcessors（如PropertyPlaceholderConfigurer）访问和修改属性值。
 */
public interface BeanDefinition {


    /**
     * Bean的类型
     *
     * @return
     */
    Class getBeanClass();


    /**
     * 是否是抽象类
     *
     * @return
     */
    boolean isAbstract();


    /**
     * 是否是单例类
     *
     * @return
     */
    boolean isSingleton();


    /**
     * 是否是懒加载
     *
     * @return
     */
    boolean isLazyInit();

    //上面的是1.1新加的

    /**
     * 返回PropertyValues以应用于bean的新实例。
     *
     * @return
     */
    MutablePropertyValues getPropertyValues();

    /**
     * 返回这个bean的构造函数参数值。
     *
     * @return
     */
    ConstructorArgumentValues getConstructorArgumentValues();

    /**
     * 返回这个bean定义的资源的描述
     *
     * @return
     */
    String getResourceDescription();


}
