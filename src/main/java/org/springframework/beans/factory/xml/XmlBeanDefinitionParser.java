package org.springframework.beans.factory.xml;

import org.springframework.beans.factory.support.BeanDefinitionParser;

/**
 * Strategy interface for parsing XML bean definitions.
 * Used by XmlBeanDefinitionReader for actually parsing a DOM document.
 * <p>
 * <p>Instantiated per document to parse: Implementations can hold state in
 * instance variables during the execution of the registerBeanDefinitions
 * method, for example global settings that are defined for all bean
 * definitions in the document.
 *
 * @author Juergen Hoeller
 * @see XmlBeanDefinitionReader#setParserClass
 * @since 18.12.2003
 */
public interface XmlBeanDefinitionParser extends BeanDefinitionParser {


}
