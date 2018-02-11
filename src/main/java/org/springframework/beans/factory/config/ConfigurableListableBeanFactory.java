package org.springframework.beans.factory.config;

import org.springframework.beans.factory.ListableBeanFactory;

public interface ConfigurableListableBeanFactory
		extends ListableBeanFactory, ConfigurableBeanFactory, AutowireCapableBeanFactory {

	/**
	 * Ensure that all non-lazy-init singletons are instantiated, also considering
	 * FactoryBeans. Typically invoked at the end of factory setup, if desired.
	 */
	void preInstantiateSingletons();

}
