/*
 * Copyright 2002-2004 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.beans.factory.config;

import org.springframework.beans.MutablePropertyValues;

/**
 * A BeanDefinition describes a bean instance, which has property values,
 * constructor argument values, and further information supplied by
 * concrete implementations.
 * <p>
 * <p>This is just a minimal interface: The main intention is to allow
 * BeanFactoryPostProcessors (like PropertyPlaceholderConfigurer) to
 * access and modify property values.
 * <p>
 * BeanDefinition描述了一个bean实例，该实例具有属性值，构造函数参数值以及由具体实现提供的更多信息。
 * <p>
 * 这只是一个最小的接口：主要目的是允许BeanFactoryPostProcessors（如PropertyPlaceholderConfigurer）访问和修改属性值。
 * <p>
 *
 * @author Juergen Hoeller
 * @see ConfigurableBeanFactory#getBeanDefinition
 * @see BeanFactoryPostProcessor
 * @see PropertyPlaceholderConfigurer
 * @see org.springframework.beans.factory.support.RootBeanDefinition
 * @see org.springframework.beans.factory.support.ChildBeanDefinition
 * @since 19.03.2004
 */
public interface BeanDefinition {


    /**
     * Return the class defined for the bean, if any.
     */
    Class getBeanClass();

    /**
     * Return whether this bean is "abstract", i.e. not meant to be instantiated.
     */
    boolean isAbstract();

    /**
     * Return whether this a <b>Singleton</b>, with a single, shared instance
     * returned on all calls.
     */
    boolean isSingleton();

    /**
     * Return whether this bean should be lazily initialized, i.e. not
     * eagerly instantiated on startup. Only applicable to a singleton bean.
     */
    boolean isLazyInit();

    //上面的是1.1新加的

    /**
     * Return the PropertyValues to be applied to a new instance of the bean.
     * 返回PropertyValues以应用于bean的新实例。
     */
    MutablePropertyValues getPropertyValues();

    /**
     * 返回这个bean的构造函数参数值。
     * Return the constructor argument values for this bean.
     */
    ConstructorArgumentValues getConstructorArgumentValues();

    /**
     * Return a description of the resource that this bean definition
     * came from (for the purpose of showing context in case of errors).
     * 返回这个bean定义的资源的描述
     * 来源(为了在出现错误的情况下显示上下文)
     */
    String getResourceDescription();


}
