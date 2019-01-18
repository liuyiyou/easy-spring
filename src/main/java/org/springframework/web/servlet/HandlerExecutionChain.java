package org.springframework.web.servlet;

/***
 *
 * @author: liuyiyou.cn
 * @date: 2019/1/18
 * @Copyright 2019 liuyiyou.cn Inc. All rights reserved
 */
public class HandlerExecutionChain {

    private Object handler;

    private HandlerInterceptor[] interceptors;

    public HandlerExecutionChain(Object handler) {
        this.handler = handler;
    }

    public HandlerExecutionChain(Object handler, HandlerInterceptor[] interceptors) {
        this.handler = handler;
        this.interceptors = interceptors;
    }

    public Object getHandler() {
        return handler;
    }


    public HandlerInterceptor[] getInterceptors() {
        return interceptors;
    }


}
