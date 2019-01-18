package org.springframework.web.context;

import org.springframework.context.ConfigurableApplicationContext;

import javax.servlet.ServletContext;

/***
 *
 * @author: liuyiyou.cn
 * @date: 2019/1/11
 * @Copyright 2019 liuyiyou.cn Inc. All rights reserved
 */
public interface ConfigurableWebApplicationContext extends WebApplicationContext, ConfigurableApplicationContext {

    String CONFIG_LOCATION_DELIMITERS = ",;";

    void setServletContext(ServletContext servletContext);

    void setNamespace(String namespace);

    void setConfigLocation(String[] location);


}
