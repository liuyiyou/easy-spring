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

package org.springframework.context.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.HierarchicalMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.ApplicationEventMulticasterImpl;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.OrderComparator;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public abstract class AbstractApplicationContext extends DefaultResourceLoader
        implements ConfigurableApplicationContext {

    public static final String MESSAGE_SOURCE_BEAN_NAME = "messageSource";


    //---------------------------------------------------------------------
    // Instance data
    //---------------------------------------------------------------------

    /**
     * Log4j logger used by this class. Available to subclasses.
     */
    protected final Log logger = LogFactory.getLog(getClass());

    /**
     * Parent context
     */
    private ApplicationContext parent;

    /**
     * BeanFactoryPostProcessors to apply on refresh
     */
    private final List beanFactoryPostProcessors = new ArrayList();

    /**
     * Display name
     */
    private String displayName = getClass().getName() + ";hashCode=" + hashCode();

    /**
     * System time in milliseconds when this context started
     */
    private long startupTime;

    /**
     * MessageSource helper we delegate our implementation of this interface to
     */
    private MessageSource messageSource;

    /**
     * Helper class used in event publishing
     */
    private final ApplicationEventMulticaster eventMulticaster = new ApplicationEventMulticasterImpl();


    //---------------------------------------------------------------------
    // Constructors
    //---------------------------------------------------------------------

    /**
     * Create a new AbstractApplicationContext with no parent.
     */
    public AbstractApplicationContext() {
    }

    /**
     * Create a new AbstractApplicationContext with the given parent context.
     *
     * @param parent parent context
     */
    public AbstractApplicationContext(ApplicationContext parent) {
        this.parent = parent;
    }


    //---------------------------------------------------------------------
    // Implementation of ApplicationContext
    //---------------------------------------------------------------------

    /**
     * Return the parent context, or null if there is no parent,
     * and this is the root of the context hierarchy.
     *
     * @return the parent context, or null if there is no parent
     */
    public ApplicationContext getParent() {
        return parent;
    }

    /**
     * To avoid endless constructor chaining, only concrete classes
     * take this in their constructor, and then invoke this method
     */
    protected void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Return a friendly name for context
     *
     * @return a display name for the context
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Return the timestamp when this context was first loaded
     *
     * @return the timestamp (ms) when this context was first loaded
     */
    public long getStartupDate() {
        return startupTime;
    }

    /**
     * Publish the given event to all listeners.
     * <p>Note: Listeners get initialized after the message source, to be able
     * to access it within listener implementations. Thus, message source
     * implementation cannot publish events.
     *
     * @param event event to publish. The event may be application-specific,
     *              or a standard framework event.
     */
    public void publishEvent(ApplicationEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("Publishing event in context [" + getDisplayName() + "]: " + event.toString());
        }
        this.eventMulticaster.onApplicationEvent(event);
        if (this.parent != null) {
            parent.publishEvent(event);
        }
    }


    //---------------------------------------------------------------------
    // Implementation of ConfigurableApplicationContext
    //---------------------------------------------------------------------

    public void setParent(ApplicationContext parent) {
        this.parent = parent;
    }

    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor beanFactoryPostProcessor) {
        this.beanFactoryPostProcessors.add(beanFactoryPostProcessor);
    }

    /**
     * Return the list of BeanPostProcessors that will get applied
     * to beans created with this factory.
     */
    public List getBeanFactoryPostProcessors() {
        return beanFactoryPostProcessors;
    }


    /**
     * 加载或重新加载配置。
     *
     * @throws BeansException
     */
    public void refresh() throws BeansException {
        this.startupTime = System.currentTimeMillis();
        //告诉子类刷新内部的bean工厂
        refreshBeanFactory();
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();
        //使用上下文语义来配置bean工厂
        //注册propertyEditor
        beanFactory.registerCustomEditor(Resource.class, new ContextResourceEditor(this));
        //注册Bean后置助力器
        beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
        //忽略该类型的依赖
        beanFactory.ignoreDependencyType(ResourceLoader.class);
        //忽略该类型的依赖
        beanFactory.ignoreDependencyType(ApplicationContext.class);
        postProcessBeanFactory(beanFactory);
        // invoke factory processors registered with the context instance
        for (Iterator it = getBeanFactoryPostProcessors().iterator(); it.hasNext(); ) {
            BeanFactoryPostProcessor factoryProcessor = (BeanFactoryPostProcessor) it.next();
            factoryProcessor.postProcessBeanFactory(beanFactory);
        }
        // invoke factory processors registered as beans in the context
        invokeBeanFactoryPostProcessors();
        // register bean processor that intercept bean creation
        registerBeanPostProcessors();
        // initialize message source for this context
        initMessageSource();
        // initialize other special beans in specific context subclasses
        onRefresh();
        // check for listener beans and register them
        refreshListeners();
        // instantiate singletons this late to allow them to access the message source
        beanFactory.preInstantiateSingletons();
        // last step: publish respective event
        publishEvent(new ContextRefreshedEvent(this));
    }

    /**
     * Modify the application context's internal bean factory after its standard
     * initialization. All bean definitions will have been loaded, but no beans
     * will have been instantiated yet. This allows for registering special
     * BeanPostProcessors etc in certain ApplicationContext implementations.
     *
     * @param beanFactory the bean factory used by the application context
     * @throws BeansException in case of errors
     */
    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    /**
     * Instantiate and invoke all registered BeanFactoryPostProcessor beans,
     * respecting explicit order if given.
     * Must be called before singleton instantiation.
     */
    private void invokeBeanFactoryPostProcessors() throws BeansException {
        String[] beanNames = getBeanDefinitionNames(BeanFactoryPostProcessor.class);
        BeanFactoryPostProcessor[] factoryProcessors = new BeanFactoryPostProcessor[beanNames.length];
        for (int i = 0; i < beanNames.length; i++) {
            factoryProcessors[i] = (BeanFactoryPostProcessor) getBean(beanNames[i]);
        }
        Arrays.sort(factoryProcessors, new OrderComparator());
        for (int i = 0; i < factoryProcessors.length; i++) {
            BeanFactoryPostProcessor factoryProcessor = factoryProcessors[i];
            factoryProcessor.postProcessBeanFactory(getBeanFactory());
        }
    }

    /**
     * Instantiate and invoke all registered BeanPostProcessor beans,
     * respecting explicit order if given.
     * Must be called before singleton instantiation.
     */
    private void registerBeanPostProcessors() throws BeansException {
        String[] beanNames = getBeanDefinitionNames(BeanPostProcessor.class);
        if (beanNames.length > 0) {
            List beanProcessors = new ArrayList();
            for (int i = 0; i < beanNames.length; i++) {
                beanProcessors.add(getBean(beanNames[i]));
            }
            Collections.sort(beanProcessors, new OrderComparator());
            for (Iterator it = beanProcessors.iterator(); it.hasNext(); ) {
                getBeanFactory().addBeanPostProcessor((BeanPostProcessor) it.next());
            }
        }
    }

    /**
     * Initialize the MessageSource.
     * Use parent's if none defined in this context.
     */
    private void initMessageSource() throws BeansException {
        try {
            this.messageSource = (MessageSource) getBean(MESSAGE_SOURCE_BEAN_NAME);
            // set parent message source if applicable,
            // and if the message source is defined in this context, not in a parent
            if (this.parent != null && (this.messageSource instanceof HierarchicalMessageSource) &&
                    Arrays.asList(getBeanDefinitionNames()).contains(MESSAGE_SOURCE_BEAN_NAME)) {
                ((HierarchicalMessageSource) this.messageSource).setParentMessageSource(this.parent);
            }
        } catch (NoSuchBeanDefinitionException ex) {
            logger.info("No MessageSource found for [" + getDisplayName() + "]: using empty StaticMessageSource");
            // use empty message source to be able to accept getMessage calls
            this.messageSource = new StaticMessageSource();
        }
    }

    /**
     * Template method which can be overridden to add context-specific refresh work.
     * Called on initialization of special beans, before instantiation of singletons.
     *
     * @throws BeansException in case of errors during refresh
     * @see #refresh
     */
    protected void onRefresh() throws BeansException {
        // for subclasses: do nothing by default
    }

    /**
     * Add beans that implement ApplicationListener as listeners.
     * Doesn't affect other listeners, which can be added without being beans.
     */
    private void refreshListeners() throws BeansException {
        logger.info("Refreshing listeners");
        Collection listeners = getBeansOfType(ApplicationListener.class, true, false).values();
        logger.debug("Found " + listeners.size() + " listeners in bean factory");
        for (Iterator it = listeners.iterator(); it.hasNext(); ) {
            ApplicationListener listener = (ApplicationListener) it.next();
            addListener(listener);
            logger.info("Application listener [" + listener + "] added");
        }
    }

    /**
     * Subclasses can invoke this method to register a listener.
     * Any beans in the context that are listeners are automatically added.
     *
     * @param listener the listener to register
     */
    protected void addListener(ApplicationListener listener) {
        this.eventMulticaster.addApplicationListener(listener);
    }

    /**
     * Destroy the singletons in the bean factory of this application context.
     */
    public void close() {
        logger.info("Closing application context [" + getDisplayName() + "]");

        // destroy all cached singletons in this context,
        // invoking DisposableBean.destroy and/or "destroy-method"
        getBeanFactory().destroySingletons();

        // publish respective event
        publishEvent(new ContextClosedEvent(this));
    }


    //---------------------------------------------------------------------
    // Implementation of BeanFactory
    //---------------------------------------------------------------------

    public Object getBean(String name) throws BeansException {
        return getBeanFactory().getBean(name);
    }

    public Object getBean(String name, Class requiredType) throws BeansException {
        return getBeanFactory().getBean(name, requiredType);
    }

    public boolean containsBean(String name) {
        return getBeanFactory().containsBean(name);
    }

    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return getBeanFactory().isSingleton(name);
    }

    public String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        return getBeanFactory().getAliases(name);
    }


    //---------------------------------------------------------------------
    // Implementation of ListableBeanFactory
    //---------------------------------------------------------------------

    public int getBeanDefinitionCount() {
        return getBeanFactory().getBeanDefinitionCount();
    }

    public String[] getBeanDefinitionNames() {
        return getBeanFactory().getBeanDefinitionNames();
    }

    public String[] getBeanDefinitionNames(Class type) {
        return getBeanFactory().getBeanDefinitionNames(type);
    }

    public boolean containsBeanDefinition(String name) {
        return getBeanFactory().containsBeanDefinition(name);
    }

    public Map getBeansOfType(Class type, boolean includePrototypes, boolean includeFactoryBeans) throws BeansException {
        return getBeanFactory().getBeansOfType(type, includePrototypes, includeFactoryBeans);
    }


    //---------------------------------------------------------------------
    // Implementation of HierarchicalBeanFactory
    //---------------------------------------------------------------------

    public BeanFactory getParentBeanFactory() {
        return getParent();
    }


    //---------------------------------------------------------------------
    // Implementation of MessageSource
    //---------------------------------------------------------------------

    public String getMessage(String code, Object args[], String defaultMessage, Locale locale) {
        return this.messageSource.getMessage(code, args, defaultMessage, locale);
    }

    public String getMessage(String code, Object args[], Locale locale) throws NoSuchMessageException {
        return this.messageSource.getMessage(code, args, locale);
    }

    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        return this.messageSource.getMessage(resolvable, locale);
    }


    /**
     * Return information about this context.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getName());
        sb.append(": ");
        sb.append("displayName=[").append(this.displayName).append("]; ");
        sb.append("startup date=[").append(new Date(this.startupTime)).append("]; ");
        if (this.parent == null) {
            sb.append("root of ApplicationContext hierarchy");
        } else {
            sb.append("parent=[").append(this.parent).append(']');
        }
        return sb.toString();
    }


    //---------------------------------------------------------------------
    // Abstract methods that must be implemented by subclasses
    //---------------------------------------------------------------------

    /**
     * Subclasses must implement this method to perform the actual configuration load.
     * The method is invoked by refresh before any other initialization work.
     *
     * @see #refresh
     */
    protected abstract void refreshBeanFactory() throws BeansException;

    /**
     * Subclasses must return their internal bean factory here.
     * They should implement the lookup efficiently, so that it can be called
     * repeatedly without a performance penalty.
     *
     * @return this application context's internal bean factory
     */
    public abstract ConfigurableListableBeanFactory getBeanFactory();

}
