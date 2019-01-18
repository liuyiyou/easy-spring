package org.springframework.web.util;

import javax.servlet.ServletContext;
import java.io.File;

/***
 *
 * @author: liuyiyou.cn
 * @date: 2019/1/11
 * @Copyright 2019 liuyiyou.cn Inc. All rights reserved
 */
public class WebUtils {

    public static final String TEMP_DIR_CONTEXT_ATTRIBUTE = "javax.servlet.context.tempdir";

    public static File getTempdir(ServletContext servletContext) {
        return (File) servletContext.getAttribute(TEMP_DIR_CONTEXT_ATTRIBUTE);

    }
}
