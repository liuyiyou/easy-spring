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

package org.springframework.beans.factory.support;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.util.ClassUtils;

/**
 * Common base class for bean definitions, factoring out common
 * functionality from RootBeanDefinition and ChildBeanDefinition.
 * 用于bean定义的通用基类，从RootBeanDefinition和ChildBeanDefinition中分解出常用的功能。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @version $Id: AbstractBeanDefinition.java,v 1.12 2004/03/19 17:45:36 jhoeller Exp $
 * @see RootBeanDefinition
 * @see ChildBeanDefinition
 */
public abstract class AbstractBeanDefinition implements BeanDefinition {

    //Bean属性值
    private MutablePropertyValues propertyValues;

    //构造函数参数值
    private ConstructorArgumentValues constructorArgumentValues;

    //资源描述
    private String resourceDescription;

    //默认是单例
    private boolean singleton = true;

    //默认是非懒加载
    private boolean lazyInit = false;


    //从RootBeanDefinition抽出来

    private Object beanClass;


    protected AbstractBeanDefinition() {
        this(null, null);
    }

    protected AbstractBeanDefinition(ConstructorArgumentValues cargs, MutablePropertyValues pvs) {
        this.constructorArgumentValues =
                (constructorArgumentValues != null ? constructorArgumentValues : new ConstructorArgumentValues());
        this.propertyValues = (pvs != null) ? pvs : new MutablePropertyValues();
    }

    /**
     * Create a new bean definition.(创建一个bean definition)
     *
     * @param pvs the PropertyValues to be applied to a new instance of the bean(将PropertyValues应用于bean的新实例)
     */
    protected AbstractBeanDefinition(MutablePropertyValues pvs) {
        this(null, pvs);
    }

    @Override
    public MutablePropertyValues getPropertyValues() {
        return propertyValues;
    }

    /**
     * This implementations returns null: Just RootBeanDefinitions
     * have concrete support for constructor argument values.
     * 在抽象类中的实现返回null，只有RootBeanDefinitions对构造函数的参数值有具体的支持。
     */
    @Override
    public ConstructorArgumentValues getConstructorArgumentValues() {
        return null;
    }

    /**
     * Set a description of the resource that this bean definition
     * came from (for the purpose of showing context in case of errors).
     * 设置这个bean定义来源的资源的描述（为了显示出错的情况）。
     */

    public void setResourceDescription(String resourceDescription) {
        this.resourceDescription = resourceDescription;
    }

    @Override
    public String getResourceDescription() {
        return resourceDescription;
    }

    /**
     * Set if this a <b>Singleton</b>, with a single, shared instance returned
     * on all calls. If false, the BeanFactory will apply the <b>Prototype</b>
     * design pattern, with each caller requesting an instance getting an
     * independent instance. How this is defined will depend on the BeanFactory.
     * "Singletons" are the commoner type.
     * 如果这是一个Singleton，则在所有调用中返回一个共享实例。
     * 如果为false，则BeanFactory将应用Prototype设计模式，
     * 每个调用者请求一个实例获取独立实例。 这是如何定义的将取决于BeanFactory。 “Singletons”是常规类型。
     */
    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    /**
     * Return whether this a <b>Singleton</b>, with a single, shared instance
     * returned on all calls,
     * 返回这个Singleton是否在所有调用中都返回一个共享实例，
     */
    public boolean isSingleton() {
        return singleton;
    }

    /**
     * Set whether this bean should be lazily initialized.
     * Only applicable to a singleton bean.
     * If false, it will get instantiated on startup by bean factories
     * that perform eager initialization of singletons.
     * 设置这个bean是否应该被延迟初始化。
     * 只适用于单例bean。 如果为false，则会在执行初始化单身人员的bean工厂启动时实例化。
     */
    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    /**
     * Return whether this bean should be lazily initialized.
     * 返回这个bean是否应该被延迟初始化。
     */
    public boolean isLazyInit() {
        return lazyInit;
    }

    /**
     * Validate this bean definition.
     *
     * @throws BeanDefinitionValidationException in case of validation failure
     */
    public void validate() throws BeanDefinitionValidationException {
        if (this.lazyInit && !this.singleton) {
            throw new BeanDefinitionValidationException("Lazy initialization is just applicable to singleton beans");
        }
    }


    public boolean hasBeanClass() {
        return (this.beanClass instanceof Class);
    }


    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }

    /**
     * 返回包装的bean的类。
     * Returns the class of the wrapped bean.
     *
     * @throws IllegalStateException if the bean definition does not carry
     *                               a resolved bean class
     */
    public final Class getBeanClass() throws IllegalStateException {
        if (!(this.beanClass instanceof Class)) {
            throw new IllegalStateException("Bean definition does not carry a resolved bean class");
        }
        return (Class) this.beanClass;
    }

    /**
     * Returns the class name of the wrapped bean.
     * 返回包装的bean的类名。
     */
    public final String getBeanClassName() {
        if (this.beanClass instanceof Class) {
            return ((Class) this.beanClass).getName();
        } else {
            return (String) this.beanClass;
        }
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClass = beanClassName;
    }


    public Class resolveBeanClass(ClassLoader classLoader) throws ClassNotFoundException {
        String className = getBeanClassName();
        if (className == null) {
            return null;
        }
        Class resolvedClass = ClassUtils.forName(className, classLoader);
        this.beanClass = resolvedClass;
        return resolvedClass;
    }




}
