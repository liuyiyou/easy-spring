package org.springframework.web.servlet;

import javax.servlet.http.HttpServletRequest;

/***
 *
 * @author: liuyiyou.cn
 * @date: 2019/1/18
 * @Copyright 2019 liuyiyou.cn Inc. All rights reserved
 */
public interface HandlerMapping {

    HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception;
}
