package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.io.Resource;

/**
 * Simple interface for bean definition readers.
 * Specifies a load method with a Resource parameter.
 * <p>
 * <p>Concrete bean definition readers can of course add additional
 * load and register methods for bean definitions, specific to
 * their bean definition format.
 *
 * @author Juergen Hoeller
 * @see org.springframework.core.io.Resource
 * @since 1.1
 */
public interface BeanDefinitionReader {

    /**
     * Return the bean factory to register the bean definitions with.
     */
    BeanDefinitionRegistry getBeanFactory();

    /**
     * Return the class loader to use for bean classes.
     * <p>Null suggests to not load bean classes but just register bean definitions
     * with class names, for example when just registering beans in a registry
     * but not actually instantiating them in a factory.
     */
    ClassLoader getBeanClassLoader();

    /**
     * Load bean definitions from the specified resource.
     *
     * @param resource the resource descriptor
     * @return the number of bean definitions found
     * @throws BeansException in case of loading or parsing errors
     */
    int loadBeanDefinitions(Resource resource) throws BeansException;
}
