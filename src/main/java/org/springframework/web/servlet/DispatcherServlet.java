package org.springframework.web.servlet;

import java.util.Map;

/***
 *
 * @author: liuyiyou.cn
 * @date: 2019/1/11
 * @Copyright 2019 liuyiyou.cn Inc. All rights reserved
 */
public class DispatcherServlet extends FrameworkServlet {



    @Override
    protected void initFrameworkServlet() {
        initHandlerMappings();
        initHandlerAdapters();
        initHandlerExceptionResolvers();
        initViewResolver();
    }

    private void initHandlerMappings() {
    }

    private void initHandlerAdapters() {
    }

    private void initHandlerExceptionResolvers() {
    }

    private void initViewResolver() {
    }


}
