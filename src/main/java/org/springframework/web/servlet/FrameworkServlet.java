package org.springframework.web.servlet;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.ConfigurableWebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/***
 *
 * @author: liuyiyou.cn
 * @date: 2019/1/11
 * @Copyright 2019 liuyiyou.cn Inc. All rights reserved
 */
@Slf4j
@Data
public abstract class FrameworkServlet extends HttpServletBean {
    private static final String DEFAULT_NAMESPACE_SUFFIX = "-servlet";
    private static final Class DEFAULT_CONTEXT_CLASS = XmlWebApplicationContext.class;
    private WebApplicationContext webApplicationContext;

    private String contextConfigLocation;
    private String namespace;
    private Class contextClass = DEFAULT_CONTEXT_CLASS;

    @Override
    protected void initServletBean() throws ServletException {
        this.webApplicationContext = initWebAppApplicationContext();
        initFrameworkServlet();
    }



    protected WebApplicationContext initWebAppApplicationContext() {
        log.info("初始化WebApplicationContext:{}", getServletName());
        ServletContext servletContext = getServletConfig().getServletContext();
        WebApplicationContext parent = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        WebApplicationContext wac = createWebApplicationContext(parent);
        return wac;

    }

    protected WebApplicationContext createWebApplicationContext(WebApplicationContext parent) throws BeansException {
        ConfigurableWebApplicationContext wac = (ConfigurableWebApplicationContext) BeanUtils.instantiateClass(getContextClass());
        wac.setParent(parent);
        wac.setServletContext(getServletContext());
        wac.setNamespace(getNamespace());
        if (this.contextConfigLocation != null) {
            wac.setConfigLocation(StringUtils.tokenizeToStringArray(this.contextConfigLocation, ConfigurableWebApplicationContext.CONFIG_LOCATION_DELIMITERS, true, true));
        }
        wac.refresh();
        return wac;

    }

    protected String getNamespace() {
        return namespace != null ? namespace : getServletName() + FrameworkServlet.DEFAULT_NAMESPACE_SUFFIX;
    }

    protected abstract void initFrameworkServlet();

}
