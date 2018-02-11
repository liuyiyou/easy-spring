package org.springframework.beans.factory.support;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;

import java.lang.reflect.Constructor;

public abstract class AbstractBeanDefinition implements BeanDefinition {

    public static final int AUTOWIRE_NO = 0;

    public static final int AUTOWIRE_BY_NAME = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;

    public static final int AUTOWIRE_BY_TYPE = AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE;

    public static final int AUTOWIRE_CONSTRUCTOR = AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR;

    public static final int AUTOWIRE_AUTODETECT = AutowireCapableBeanFactory.AUTOWIRE_AUTODETECT;


    public static final int DEPENDENCY_CHECK_NONE = 0;

    public static final int DEPENDENCY_CHECK_OBJECTS = 1;

    public static final int DEPENDENCY_CHECK_SIMPLE = 2;

    public static final int DEPENDENCY_CHECK_ALL = 3;


    private Object beanClass;

    private boolean abstractFlag = false;

    private boolean singleton = true;

    private boolean lazyInit = false;

    private ConstructorArgumentValues constructorArgumentValues;

    private MutablePropertyValues propertyValues;

//    private MethodOverrides methodOverrides = new MethodOverrides();

    private String initMethodName;

    private String destroyMethodName;

    private String factoryMethodName;

    private String factoryBeanName;

    private int autowireMode = AUTOWIRE_NO;

    private int dependencyCheck = DEPENDENCY_CHECK_NONE;

    private String[] dependsOn;

    private String resourceDescription;


    protected AbstractBeanDefinition() {
        setConstructorArgumentValues(new ConstructorArgumentValues());
        setPropertyValues(new MutablePropertyValues());
    }

    protected AbstractBeanDefinition(AbstractBeanDefinition other) {
        this.beanClass = other.beanClass;

        setAbstract(other.isAbstract());
        setSingleton(other.isSingleton());
        setLazyInit(other.isLazyInit());

        setConstructorArgumentValues(new ConstructorArgumentValues(other.getConstructorArgumentValues()));
        setPropertyValues(new MutablePropertyValues(other.getPropertyValues()));
//        setMethodOverrides(new MethodOverrides(other.getMethodOverrides()));

        setInitMethodName(other.getInitMethodName());
        setDestroyMethodName(other.getDestroyMethodName());
        setFactoryMethodName(other.getFactoryMethodName());
        setFactoryBeanName(other.getFactoryBeanName());

        setDependsOn(other.getDependsOn());
        setAutowireMode(other.getAutowireMode());
        setDependencyCheck(other.getDependencyCheck());

        setResourceDescription(other.getResourceDescription());
    }

    public void overrideFrom(AbstractBeanDefinition other) {
        if (other.beanClass != null) {
            this.beanClass = other.beanClass;
        }
        setAbstract(other.isAbstract());
        setSingleton(other.isSingleton());
        setLazyInit(other.isLazyInit());
        getConstructorArgumentValues().addArgumentValues(other.getConstructorArgumentValues());
        getPropertyValues().addPropertyValues(other.getPropertyValues());
//        getMethodOverrides().addOverrides(other.getMethodOverrides());
        if (other.getInitMethodName() != null) {
            setInitMethodName(other.getInitMethodName());
        }
        if (other.getDestroyMethodName() != null) {
            setDestroyMethodName(other.getDestroyMethodName());
        }
        if (other.getFactoryMethodName() != null) {
            setFactoryMethodName(other.getFactoryMethodName());
        }
        if (other.getFactoryBeanName() != null) {
            setFactoryBeanName(other.getFactoryBeanName());
        }

        setDependsOn(other.getDependsOn());
        setAutowireMode(other.getAutowireMode());
        setDependencyCheck(other.getDependencyCheck());

        setResourceDescription(other.getResourceDescription());
    }


    public boolean hasBeanClass() {
        return (this.beanClass instanceof Class);
    }

    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }

    public Class getBeanClass() throws IllegalStateException {
        if (!(this.beanClass instanceof Class)) {
            throw new IllegalStateException("Bean definition does not carry a resolved bean class");
        }
        return (Class) this.beanClass;
    }


    public void setBeanClassName(String beanClassName) {
        this.beanClass = beanClassName;
    }


    public String getBeanClassName() {
        if (this.beanClass instanceof Class) {
            return ((Class) this.beanClass).getName();
        } else {
            return (String) this.beanClass;
        }
    }


    public void setAbstract(boolean abstractFlag) {
        this.abstractFlag = abstractFlag;
    }


    public boolean isAbstract() {
        return abstractFlag;
    }


    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }


    public ConstructorArgumentValues getConstructorArgumentValues() {
        return constructorArgumentValues;
    }


    public boolean hasConstructorArgumentValues() {
        return (constructorArgumentValues != null && !constructorArgumentValues.isEmpty());
    }


    public void setPropertyValues(MutablePropertyValues propertyValues) {
        this.propertyValues = (propertyValues != null) ? propertyValues : new MutablePropertyValues();
    }

    public MutablePropertyValues getPropertyValues() {
        return propertyValues;
    }


    public void setInitMethodName(String initMethodName) {
        this.initMethodName = initMethodName;
    }

    public String getInitMethodName() {
        return this.initMethodName;
    }

    public void setDestroyMethodName(String destroyMethodName) {
        this.destroyMethodName = destroyMethodName;
    }

    public String getDestroyMethodName() {
        return this.destroyMethodName;
    }

    public void setFactoryMethodName(String factoryMethodName) {
        this.factoryMethodName = factoryMethodName;
    }

    public String getFactoryMethodName() {
        return this.factoryMethodName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }


    public void setAutowireMode(int autowireMode) {
        this.autowireMode = autowireMode;
    }

    public void setConstructorArgumentValues(ConstructorArgumentValues constructorArgumentValues) {
        this.constructorArgumentValues = (constructorArgumentValues != null) ?
                constructorArgumentValues : new ConstructorArgumentValues();
    }

    public int getAutowireMode() {
        return autowireMode;
    }


    public int getResolvedAutowireMode() {
        if (this.autowireMode == AUTOWIRE_AUTODETECT) {
            Constructor[] constructors = getBeanClass().getConstructors();
            for (int i = 0; i < constructors.length; i++) {
                if (constructors[i].getParameterTypes().length == 0) {
                    return AUTOWIRE_BY_TYPE;
                }
            }
            return AUTOWIRE_CONSTRUCTOR;
        } else {
            return this.autowireMode;
        }
    }

    public void setDependencyCheck(int dependencyCheck) {
        this.dependencyCheck = dependencyCheck;
    }

    public int getDependencyCheck() {
        return dependencyCheck;
    }

    public void setDependsOn(String[] dependsOn) {
        this.dependsOn = dependsOn;
    }

    public String[] getDependsOn() {
        return dependsOn;
    }

    public void setResourceDescription(String resourceDescription) {
        this.resourceDescription = resourceDescription;
    }


    public String getResourceDescription() {
        return resourceDescription;
    }

    public void validate() throws BeanDefinitionValidationException {
        if (this.lazyInit && !this.singleton) {
            throw new BeanDefinitionValidationException("Lazy initialization is applicable only to singleton beans");
        }
    }

}
