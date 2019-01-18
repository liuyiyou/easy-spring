package org.springframework.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/***
 *
 * 视图接口
 * @author: liuyiyou.cn
 * @date: 2019/1/11
 * @Copyright 2019 liuyiyou.cn Inc. All rights reserved
 */
public interface View {

    /**
     * 渲染视图
     * @param model
     * @param request
     * @param response
     * @throws Exception
     */
    void render(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception;
}
