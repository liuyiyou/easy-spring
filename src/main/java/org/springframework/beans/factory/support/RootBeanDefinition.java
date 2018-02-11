package org.springframework.beans.factory.support;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ConstructorArgumentValues;


public class RootBeanDefinition extends AbstractBeanDefinition {

    /**
     * Create a new RootBeanDefinition for a singleton,
     * using the given autowire mode.
     * @param beanClass the class of the bean to instantiate
     * @param autowireMode by name or type, using the constants in this interface
     */
    public RootBeanDefinition(Class beanClass, int autowireMode) {
        setBeanClass(beanClass);
        setPropertyValues(null);
        setAutowireMode(autowireMode);
    }

    /**
     * Create a new RootBeanDefinition for a singleton,
     * using the given autowire mode.
     * @param beanClass the class of the bean to instantiate
     * @param autowireMode by name or type, using the constants in this interface
     * @param dependencyCheck whether to perform a dependency check for objects
     * (not applicable to autowiring a constructor, thus ignored there)
     */
    public RootBeanDefinition(Class beanClass, int autowireMode, boolean dependencyCheck) {
        setBeanClass(beanClass);
        setPropertyValues(null);
        setAutowireMode(autowireMode);
        if (dependencyCheck && getResolvedAutowireMode() != AUTOWIRE_CONSTRUCTOR) {
            setDependencyCheck(RootBeanDefinition.DEPENDENCY_CHECK_OBJECTS);
        }
    }

    /**
     * Create a new RootBeanDefinition for a singleton,
     * providing property values.
     * @param beanClass the class of the bean to instantiate
     * @param pvs the property values to apply
     */
    public RootBeanDefinition(Class beanClass, MutablePropertyValues pvs) {
        setBeanClass(beanClass);
        setPropertyValues(pvs);
    }

    /**
     * Create a new RootBeanDefinition with the given singleton status,
     * providing property values.
     * @param beanClass the class of the bean to instantiate
     * @param pvs the property values to apply
     * @param singleton the singleton status of the bean
     */
    public RootBeanDefinition(Class beanClass, MutablePropertyValues pvs, boolean singleton) {
        setBeanClass(beanClass);
        setPropertyValues(pvs);
        setSingleton(singleton);
    }

    /**
     * Create a new RootBeanDefinition for a singleton,
     * providing constructor arguments and property values.
     * @param beanClass the class of the bean to instantiate
     * @param cargs the constructor argument values to apply
     * @param pvs the property values to apply
     */
    public RootBeanDefinition(Class beanClass, ConstructorArgumentValues cargs, MutablePropertyValues pvs) {
        setBeanClass(beanClass);
        setConstructorArgumentValues(cargs);;
        setPropertyValues(pvs);
    }

    /**
     * Create a new RootBeanDefinition for a singleton,
     * providing constructor arguments and property values.
     * Takes a bean class name to avoid eager loading of the bean class.
     * @param beanClassName the name of the class to instantiate
     * @param cargs the constructor argument values to apply
     * @param pvs the property values to apply
     */
    public RootBeanDefinition(String beanClassName, ConstructorArgumentValues cargs, MutablePropertyValues pvs) {
        setBeanClassName(beanClassName);
        setConstructorArgumentValues(cargs);;
        setPropertyValues(pvs);
    }

    /**
     * Deep copy constructor.
     */
    public RootBeanDefinition(RootBeanDefinition other) {
        super(other);
    }


    public void validate() throws BeanDefinitionValidationException {
        super.validate();

        if (hasBeanClass()) {
            if (FactoryBean.class.isAssignableFrom(getBeanClass()) && !isSingleton()) {
                throw new BeanDefinitionValidationException("FactoryBean must be defined as singleton: " +
                        "FactoryBeans themselves are not allowed to be prototypes");
            }
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("Root bean with class [");
        sb.append(getBeanClassName()).append(']');
        if (getResourceDescription() != null) {
            sb.append(" defined in ").append(getResourceDescription());
        }
        return sb.toString();
    }

}
