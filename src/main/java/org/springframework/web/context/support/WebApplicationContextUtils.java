package org.springframework.web.context.support;

import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;

/***
 *
 * @author: liuyiyou.cn
 * @date: 2019/1/11
 * @Copyright 2019 liuyiyou.cn Inc. All rights reserved
 */
public abstract class WebApplicationContextUtils {

    public static WebApplicationContext getWebApplicationContext(ServletContext servletContext) {
        Object attribute = servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATIONCONTEXT_ATTRIBUTE);
        if (attribute == null) {
            return null;
        }
        if (attribute instanceof RuntimeException) {
            throw (RuntimeException) attribute;
        }
        if (!(attribute instanceof WebApplicationContext)) {
            throw new IllegalArgumentException("WebApplicationContext");
        }
        return (WebApplicationContext) attribute;
    }
}
