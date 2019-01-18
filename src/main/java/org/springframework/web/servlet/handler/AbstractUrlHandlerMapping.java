package org.springframework.web.servlet.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.util.PathMatcher;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/***
 *
 * @author: liuyiyou.cn
 * @date: 2019/1/18
 * @Copyright 2019 liuyiyou.cn Inc. All rights reserved
 */
@Slf4j
public class AbstractUrlHandlerMapping extends AbstractHandlerMapping {

    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    private Map<String, String> handlerMap = new HashMap<>();

    @Override
    protected Object getHandlerInternal(HttpServletRequest request) throws BeansException {
        String lookupPath = this.urlPathHelper.getLookupPathForRequest(request);
        return lookupHandler(lookupPath);
    }

    private Object lookupHandler(String lookupPath) throws BeansException {
        String handler = this.handlerMap.get(lookupPath);
        if (handler == null) {
            for (Iterator<String> iterator = this.handlerMap.keySet().iterator(); iterator.hasNext(); ) {
                String registerdPath = iterator.next();
                if (PathMatcher.match(registerdPath, lookupPath)) {
                    handler = this.handlerMap.get(registerdPath);
                }
            }
        }
        return handler;
    }
}
