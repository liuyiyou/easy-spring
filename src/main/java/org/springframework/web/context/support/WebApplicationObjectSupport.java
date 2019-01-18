package org.springframework.web.context.support;

import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.web.util.WebUtils;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.io.File;

/***
 *
 * @author: liuyiyou.cn
 * @date: 2019/1/18
 * @Copyright 2019 liuyiyou.cn Inc. All rights reserved
 */
public class WebApplicationObjectSupport extends ApplicationObjectSupport {

    protected Class requiredContextClass() {
        return WebApplicationContext.class;
    }

    protected final WebApplicationContext getWebApplicationContext() {
        return (WebApplicationContext) getApplicationContext();
    }

    protected final ServletContext getServletContext() {
        return getWebApplicationContext().getServletContext();
    }

    protected final File getTempDir() {
        return WebUtils.getTempdir(getServletContext());
    }
}
