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

package org.springframework.beans.factory;

import org.springframework.beans.BeansException;

/**
 * Exception thrown when a bean instance has been requested for a bean
 * which has been defined as abstract
 * @author Juergen Hoeller
 * @since 1.1
 * @see org.springframework.beans.factory.support.AbstractBeanDefinition#setAbstract
 */
public class BeanIsAbstractException extends BeansException {

	/**
	 * Create a new <code>BeanIsAbstractException</code>.
	 * @param name the name of the bean requested
	 */
	public BeanIsAbstractException(String name) {
		super("Tried to instantiate abstract bean definition '" + name + "'");
	}

}