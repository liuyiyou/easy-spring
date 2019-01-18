package org.springframework.beans.factory.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanIsNotAFactoryException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanCircularReferenceException;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Slf4j
public abstract class AbstractBeanFactory implements ConfigurableBeanFactory, HierarchicalBeanFactory {


    private BeanFactory parentBeanFactory;


    private Map customEditors = new HashMap();


    private final Set ignoreDependencyTypes = new HashSet();


    private final List beanPostProcessors = new ArrayList();


    private final Map aliasMap = Collections.synchronizedMap(new HashMap());


    private final Map singletonCache = Collections.synchronizedMap(new HashMap());


    public AbstractBeanFactory() {
        ignoreDependencyType(BeanFactory.class);
    }


    public AbstractBeanFactory(BeanFactory parentBeanFactory) {
        this();
        this.parentBeanFactory = parentBeanFactory;
    }


    //---------------------------------------------------------------------
    // Implementation of BeanFactory
    //---------------------------------------------------------------------


    @Override
    public Object getBean(String name) throws BeansException {
        //返回bean名称，必要时剥离工厂解引用前缀，并将别名解析为规范名称。
        String beanName = transformedBeanName(name);
        // eagerly check singleton cache for manually registered singletons
        //优先从缓存中获取单例bean
        Object sharedInstance = this.singletonCache.get(beanName);
        if (sharedInstance != null) {
            if (log.isDebugEnabled()) {
                log.debug("Returning cached instance of singleton bean '" + beanName + "'");
            }
            //从缓存中获取
            return getObjectForSharedInstance(name, sharedInstance);
        } else {
            // check if bean definition exists
            //检查bean定义是否存在
            RootBeanDefinition mergedBeanDefinition = null;
            try {
                mergedBeanDefinition = getMergedBeanDefinition(beanName, false);
            } catch (NoSuchBeanDefinitionException ex) {
                // not found -> check parent
                if (this.parentBeanFactory != null) {
                    //从上级BeanFactory获取
                    return this.parentBeanFactory.getBean(name);
                }
                throw ex;
            }
            // create bean instance
            //如果是单例，则实例化bean
            if (mergedBeanDefinition.isSingleton()) {
                synchronized (this.singletonCache) {
                    // re-check singleton cache within synchronized block
                    sharedInstance = this.singletonCache.get(beanName);
                    if (sharedInstance == null) {
                        log.info("Creating shared instance of singleton bean '" + beanName + "'");
                        sharedInstance = createBean(beanName, mergedBeanDefinition);
                        addSingleton(beanName, sharedInstance);
                    }
                }
                return getObjectForSharedInstance(name, sharedInstance);
            } else {
                return createBean(name, mergedBeanDefinition);
            }
        }
    }

    @Override
    public <T> T getBean(String name, Class requiredType) throws BeansException {
        Object bean = getBean(name);
        if (!requiredType.isAssignableFrom(bean.getClass())) {
            throw new BeanNotOfRequiredTypeException(name, requiredType, bean);
        }
        return (T) bean;
    }

    @Override
    public boolean containsBean(String name) {
        String beanName = transformedBeanName(name);
        if (this.singletonCache.containsKey(beanName)) {
            return true;
        }
        if (containsBeanDefinition(beanName)) {
            return true;
        } else {
            // not found -> check parent
            if (this.parentBeanFactory != null) {
                return this.parentBeanFactory.containsBean(beanName);
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        String beanName = transformedBeanName(name);
        try {
            Class beanClass = null;
            boolean singleton = true;
            Object beanInstance = this.singletonCache.get(beanName);
            if (beanInstance != null) {
                beanClass = beanInstance.getClass();
                singleton = true;
            } else {
                RootBeanDefinition bd = getMergedBeanDefinition(beanName, false);
                beanClass = bd.getBeanClass();
                singleton = bd.isSingleton();
            }
            // in case of FactoryBean, return singleton status of created object if not a dereference
            if (FactoryBean.class.isAssignableFrom(beanClass) && !isFactoryDereference(name)) {
                FactoryBean factoryBean = (FactoryBean) getBean(FACTORY_BEAN_PREFIX + beanName);
                return factoryBean.isSingleton();
            } else {
                return singleton;
            }
        } catch (NoSuchBeanDefinitionException ex) {
            // not found -> check parent
            if (this.parentBeanFactory != null) {
                return this.parentBeanFactory.isSingleton(beanName);
            }
            throw ex;
        }
    }

    @Override
    public String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        String beanName = transformedBeanName(name);
        // check if bean actually exists in this bean factory
        if (this.singletonCache.containsKey(beanName) || containsBeanDefinition(beanName)) {
            // if found, gather aliases
            List aliases = new ArrayList();
            for (Iterator it = this.aliasMap.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry entry = (Map.Entry) it.next();
                if (entry.getValue().equals(beanName)) {
                    aliases.add(entry.getKey());
                }
            }
            return (String[]) aliases.toArray(new String[aliases.size()]);
        } else {
            // not found -> check parent
            if (this.parentBeanFactory != null) {
                return this.parentBeanFactory.getAliases(beanName);
            }
            throw new NoSuchBeanDefinitionException(beanName, toString());
        }
    }


    //---------------------------------------------------------------------
    // Implementation of HierarchicalBeanFactory
    //---------------------------------------------------------------------

    @Override
    public BeanFactory getParentBeanFactory() {
        return parentBeanFactory;
    }

    //---------------------------------------------------------------------
    // Implementation of ConfigurableBeanFactory
    //---------------------------------------------------------------------
    @Override
    public void setParentBeanFactory(BeanFactory parentBeanFactory) {
        this.parentBeanFactory = parentBeanFactory;
    }

    @Override
    public void registerCustomEditor(Class requiredType, PropertyEditor propertyEditor) {
        this.customEditors.put(requiredType, propertyEditor);
    }

    /**
     * Return the map of custom editors, with Classes as keys
     * and PropertyEditors as values.
     */


    public Map getCustomEditors() {
        return customEditors;
    }

    @Override
    public void ignoreDependencyType(Class type) {
        this.ignoreDependencyTypes.add(type);
    }

    /**
     * Return the set of classes that will get ignored for autowiring.
     */

    public Set getIgnoredDependencyTypes() {
        return ignoreDependencyTypes;
    }

    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessors.add(beanPostProcessor);
    }

    /**
     * Return the list of BeanPostProcessors that will get applied
     * to beans created with this factory.
     */

    public List getBeanPostProcessors() {
        return beanPostProcessors;
    }

    @Override
    public void registerAlias(String beanName, String alias) throws BeanDefinitionStoreException {
        log.debug("Registering alias '" + alias + "' for bean with name '" + beanName + "'");
        synchronized (this.aliasMap) {
            Object registeredName = this.aliasMap.get(alias);
            if (registeredName != null) {
                throw new BeanDefinitionStoreException("Cannot register alias '" + alias + "' for bean name '" + beanName +
                        "': it's already registered for bean name '" + registeredName + "'");
            }
            this.aliasMap.put(alias, beanName);
        }
    }

    @Override
    public void registerSingleton(String beanName, Object singletonObject) throws BeanDefinitionStoreException {
        synchronized (this.singletonCache) {
            Object oldObject = this.singletonCache.get(beanName);
            if (oldObject != null) {
                throw new BeanDefinitionStoreException("Could not register object [" + singletonObject +
                        "] under bean name '" + beanName + "': there's already object [" +
                        oldObject + " bound");
            }
            addSingleton(beanName, singletonObject);
        }
    }

    /**
     * Add the given singleton object to the singleton cache of this factory.
     * <p>To be called for eager registration of singletons, e.g. to be able to
     * resolve circular references.
     *
     * @param beanName        the name of the bean
     * @param singletonObject the singleton object
     */

    protected void addSingleton(String beanName, Object singletonObject) {
        this.singletonCache.put(beanName, singletonObject);
    }

    @Override
    public void destroySingletons() {
        if (log.isInfoEnabled()) {
            log.info("Destroying singletons in factory {" + this + "}");
        }
        synchronized (this.singletonCache) {
            Set singletonCacheKeys = new HashSet(this.singletonCache.keySet());
            for (Iterator it = singletonCacheKeys.iterator(); it.hasNext(); ) {
                destroySingleton((String) it.next());
            }
        }
    }

    /**
     * Destroy the given bean. Delegates to destroyBean if a corresponding
     * singleton instance is found.
     *
     * @param beanName name of the bean
     * @see #destroyBean
     */
    protected void destroySingleton(String beanName) {
        Object singletonInstance = this.singletonCache.remove(beanName);
        if (singletonInstance != null) {
            destroyBean(beanName, singletonInstance);
        }
    }


    //---------------------------------------------------------------------
    // Implementation methods
    //---------------------------------------------------------------------

    /**
     * Return the bean name, stripping out the factory dereference prefix if necessary,
     * and resolving aliases to canonical names.
     * <p>
     * 返回bean名称，必要时剥离工厂解引用前缀，并将别名解析为规范名称。
     */

    protected String transformedBeanName(String name) throws NoSuchBeanDefinitionException {
        if (name == null) {
            throw new NoSuchBeanDefinitionException(name, "Cannot get bean with null name");
        }
        if (name.startsWith(FACTORY_BEAN_PREFIX)) {
            name = name.substring(FACTORY_BEAN_PREFIX.length());
        }
        // handle aliasing
        String canonicalName = (String) this.aliasMap.get(name);
        return canonicalName != null ? canonicalName : name;
    }

    /**
     * Return whether this name is a factory dereference
     * (beginning with the factory dereference prefix).
     */
    protected boolean isFactoryDereference(String name) {
        return name.startsWith(FACTORY_BEAN_PREFIX);
    }


    protected void initBeanWrapper(BeanWrapper bw) {
        for (Iterator it = this.customEditors.keySet().iterator(); it.hasNext(); ) {
            Class clazz = (Class) it.next();
            bw.registerCustomEditor(clazz, (PropertyEditor) this.customEditors.get(clazz));
        }
    }


    public String[] getSingletonNames(Class type) {
        Set keys = this.singletonCache.keySet();
        Set matches = new HashSet();
        Iterator itr = keys.iterator();
        while (itr.hasNext()) {
            String name = (String) itr.next();
            Object singletonObject = this.singletonCache.get(name);
            if (type == null || type.isAssignableFrom(singletonObject.getClass())) {
                matches.add(name);
            }
        }
        return (String[]) matches.toArray(new String[matches.size()]);
    }


    protected Object getObjectForSharedInstance(String name, Object beanInstance) {
        String beanName = transformedBeanName(name);

        // Don't let calling code try to dereference the
        // bean factory if the bean isn't a factory
        if (isFactoryDereference(name) && !(beanInstance instanceof FactoryBean)) {
            throw new BeanIsNotAFactoryException(beanName, beanInstance);
        }

        // Now we have the bean instance, which may be a normal bean
        // or a FactoryBean. If it's a FactoryBean, we use it to
        // create a bean instance, unless the caller actually wants
        // a reference to the factory.
        if (beanInstance instanceof FactoryBean) {
            if (!isFactoryDereference(name)) {
                // return bean instance from factory
                FactoryBean factory = (FactoryBean) beanInstance;
                log.debug("Bean with name '" + beanName + "' is a factory bean");
                try {
                    beanInstance = factory.getObject();
                } catch (BeansException ex) {
                    throw ex;
                } catch (Exception ex) {
                    throw new BeanCreationException("FactoryBean threw exception on object creation", ex);
                }
                if (beanInstance == null) {
                    throw new FactoryBeanCircularReferenceException(
                            "Factory bean '" + beanName + "' returned null object - " +
                                    "possible cause: not fully initialized due to circular bean reference");
                }
            } else {
                // the user wants the factory itself
                log.debug("Calling code asked for FactoryBean instance for name '" + beanName + "'");
            }
        }

        return beanInstance;
    }


    public RootBeanDefinition getMergedBeanDefinition(String beanName, boolean includingAncestors)
            throws BeansException {
        try {
            return getMergedBeanDefinition(beanName, getBeanDefinition(beanName));
        } catch (NoSuchBeanDefinitionException ex) {
            if (includingAncestors && getParentBeanFactory() instanceof AbstractAutowireCapableBeanFactory) {
                return ((AbstractAutowireCapableBeanFactory) getParentBeanFactory()).getMergedBeanDefinition(beanName, true);
            } else {
                throw ex;
            }
        }
    }


    protected RootBeanDefinition getMergedBeanDefinition(String beanName, BeanDefinition bd) {
        if (bd instanceof RootBeanDefinition) {
            return (RootBeanDefinition) bd;
        } else if (bd instanceof ChildBeanDefinition) {
            ChildBeanDefinition cbd = (ChildBeanDefinition) bd;
            // deep copy
            RootBeanDefinition rbd = new RootBeanDefinition(getMergedBeanDefinition(cbd.getParentName(), true));
            // override properties
            for (int i = 0; i < cbd.getPropertyValues().getPropertyValues().length; i++) {
                rbd.getPropertyValues().addPropertyValue(cbd.getPropertyValues().getPropertyValues()[i]);
            }
            // override settings
            rbd.setSingleton(cbd.isSingleton());
            rbd.setLazyInit(cbd.isLazyInit());
            rbd.setResourceDescription(cbd.getResourceDescription());
            return rbd;
        } else {
            throw new BeanDefinitionStoreException(bd.getResourceDescription(), beanName,
                    "Definition is neither a RootBeanDefinition nor a ChildBeanDefinition");
        }
    }

    //---------------------------------------------------------------------
    // Abstract methods to be implemented by concrete subclasses
    //---------------------------------------------------------------------


    public abstract boolean containsBeanDefinition(String beanName);


    public abstract BeanDefinition getBeanDefinition(String beanName) throws BeansException;


    protected abstract Object createBean(String beanName, RootBeanDefinition mergedBeanDefinition)
            throws BeansException;

    protected abstract void destroyBean(String beanName, Object bean);

}
