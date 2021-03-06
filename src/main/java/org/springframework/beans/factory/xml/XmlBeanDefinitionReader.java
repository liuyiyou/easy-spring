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

package org.springframework.beans.factory.xml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;


public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {

    protected final Log logger = LogFactory.getLog(getClass());

    private boolean validating = true;

    private EntityResolver entityResolver;

    private Class parserClass = DefaultXmlBeanDefinitionParser.class;


    public XmlBeanDefinitionReader(BeanDefinitionRegistry beanFactory) {
        super(beanFactory);
    }


    public void setValidating(boolean validating) {
        this.validating = validating;
    }


    public void setEntityResolver(EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }

    public void setParserClass(Class parserClass) {
        if (this.parserClass == null || !XmlBeanDefinitionParser.class.isAssignableFrom(parserClass)) {
            throw new IllegalArgumentException("parserClass must be a XmlBeanDefinitionParser");
        }
        this.parserClass = parserClass;
    }


    public int loadBeanDefinitions(Resource resource) throws BeansException {
        if (resource == null) {
            throw new BeanDefinitionStoreException("Resource cannot be null: expected an XML file");
        }
        InputStream is = null;
        try {
            logger.info("Loading XML bean definitions from " + resource + "");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            logger.debug("Using JAXP implementation [" + factory + "]");
            factory.setValidating(this.validating);
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            docBuilder.setErrorHandler(new BeansErrorHandler());
            docBuilder.setEntityResolver(this.entityResolver != null ? this.entityResolver : new BeansDtdResolver());
            is = resource.getInputStream();
            Document doc = docBuilder.parse(is);
            return registerBeanDefinitions(doc, resource);
        } catch (ParserConfigurationException ex) {
            throw new BeanDefinitionStoreException("Parser configuration exception parsing XML from " + resource, ex);
        } catch (SAXParseException ex) {
            throw new BeanDefinitionStoreException("Line " + ex.getLineNumber() + " in XML document from " + resource + " is invalid", ex);
        } catch (SAXException ex) {
            throw new BeanDefinitionStoreException("XML document from " + resource + " is invalid", ex);
        } catch (IOException ex) {
            throw new BeanDefinitionStoreException("IOException parsing XML document from " + resource, ex);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    logger.warn("Could not close InputStream", ex);
                }
            }
        }
    }

    public int registerBeanDefinitions(Document doc, Resource resource) throws BeansException {
        XmlBeanDefinitionParser parser = (XmlBeanDefinitionParser) BeanUtils.instantiateClass(this.parserClass);
        return parser.registerBeanDefinitions(this, doc, resource);
    }


    private static class BeansErrorHandler implements ErrorHandler {


        private final static Log logger = LogFactory.getLog(XmlBeanFactory.class);

        public void error(SAXParseException ex) throws SAXException {
            throw ex;
        }

        public void fatalError(SAXParseException ex) throws SAXException {
            throw ex;
        }

        public void warning(SAXParseException ex) throws SAXException {
            logger.warn("Ignored XML validation warning: " + ex);
        }
    }

}
