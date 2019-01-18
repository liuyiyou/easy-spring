package org.springframework.web.context;

import org.springframework.context.ApplicationContext;

import javax.servlet.ServletContext;

/***
 *
 * @author: liuyiyou.cn
 * @date: 2019/1/11
 * @Copyright 2019 liuyiyou.cn Inc. All rights reserved
 */
public interface WebApplicationContext extends ApplicationContext {

    String ROOT_WEB_APPLICATIONCONTEXT_ATTRIBUTE = WebApplicationContext.class + ".ROOT";

    ServletContext getServletContext();
}
