package org.springframework.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/***
 *
 * @author: liuyiyou.cn
 * @date: 2019/1/18
 * @Copyright 2019 liuyiyou.cn Inc. All rights reserved
 */
public interface HandlerAdapter {


    boolean supports(Object hander);

    ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception;

    long getLastModified(HttpServletRequest request, Object handler);
}
