package org.springframework.web.servlet;

import org.springframework.core.OrderComparator;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/***
 *
 * @author: liuyiyou.cn
 * @date: 2019/1/11
 * @Copyright 2019 liuyiyou.cn Inc. All rights reserved
 */
public class DispatcherServlet extends FrameworkServlet {


    private List<HandlerMapping> handlerMappings;

    private List<HandlerAdapter> handlerAdpaters;


    @Override
    protected void initFrameworkServlet() {
        initHandlerMappings();
        initHandlerAdapters();
        initHandlerExceptionResolvers();
        initViewResolver();
    }

    private void initHandlerMappings() {
        Map<String, HandlerMapping> handlerMappingBeanMap = getWebApplicationContext().getBeansOfType(HandlerMapping.class, true, false);
        this.handlerMappings = new ArrayList(handlerMappingBeanMap.values());
        if (this.handlerMappings.isEmpty()) {
            BeanNameUrlHandlerMapping mapping = new BeanNameUrlHandlerMapping();
            mapping.setApplicationContext(getWebApplicationContext());
            this.handlerMappings.add(mapping);
        } else {
            this.handlerMappings.sort(new OrderComparator());
        }
    }

    private void initHandlerAdapters() {
    }

    private void initHandlerExceptionResolvers() {
    }

    private void initViewResolver() {
    }





}
