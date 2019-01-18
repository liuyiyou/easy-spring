package org.springframework.web.servlet.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

/***
 *
 * @author: liuyiyou.cn
 * @date: 2019/1/18
 * @Copyright 2019 liuyiyou.cn Inc. All rights reserved
 */
@Slf4j
public abstract class AbstractHandlerMapping extends WebApplicationObjectSupport implements HandlerMapping, Ordered {

    private int order = Integer.MAX_VALUE;

    private Object defaulHandler;

    private HandlerInterceptor[] interceptors;

    @Override
    public int getOrder() {
        return order;
    }

    public final void setOrder(int order) {
        this.order = order;
    }

    public void setDefautHandler(Object defaulHandler) {
        this.defaulHandler = defaulHandler;
        log.info("默认Mapping映射到[" + this.defaulHandler + "]");
    }

    public void setInterceptors(HandlerInterceptor[] interceptors) {
        this.interceptors = interceptors;
    }

    @Override
    public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        Object handlerInternal = getHandlerInternal(request);
        if (handlerInternal == null) {
            handlerInternal = this.defaulHandler;
        }
        if (handlerInternal == null) {
            log.info("没有handler");
            return null;
        }
        if (handlerInternal instanceof String) {
            String handerName = (String) handlerInternal;
            handlerInternal = getApplicationContext().getBean(handerName);
        }
        return new HandlerExecutionChain(handlerInternal, this.interceptors);
    }

    protected abstract Object getHandlerInternal(HttpServletRequest request) throws Exception;
}
