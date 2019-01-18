package org.springframework.beans.factory.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;


public abstract class AbstractFactoryBean implements FactoryBean, InitializingBean {

    protected final Log logger = LogFactory.getLog(getClass());

    private boolean singleton = true;

    private Object singletonInstance;


    public final void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    public final boolean isSingleton() {
        return singleton;
    }

    public final void afterPropertiesSet() throws Exception {
        if (singletonInstance == null) {
            this.singletonInstance = createInstance();
        }
    }

    public final Object getObject() throws Exception {
        if (isSingleton()) {
            return this.singletonInstance;
        } else {
            return createInstance();
        }
    }


    protected abstract Object createInstance() throws Exception;

}
