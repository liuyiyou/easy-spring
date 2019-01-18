package org.springframework.web.servlet;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/***
 *
 * @author: liuyiyou.cn
 * @date: 2019/1/11
 * @Copyright 2019 liuyiyou.cn Inc. All rights reserved
 */
@Slf4j
public class HttpServletBean extends HttpServlet {

    @Override
    public void init() throws ServletException {
        log.info("初始化Servlet {}", getServletName());
        initServletBean();
        log.info("Servlet {} 配置完成", getServletName());
    }


    protected void initServletBean() throws ServletException {
    }
}
