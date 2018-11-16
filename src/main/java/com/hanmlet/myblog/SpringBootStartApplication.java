package com.hanmlet.myblog;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * FileName : SpringBootStartApplication
 *
 * @author : hanml
 * @date : 2018/11/12 15:38
 * Description :
 * Copyright (C), 2015-2018, asiaInfo
 */
public class SpringBootStartApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        // 注意这里要指向原先用main方法执行的Application启动类
        return builder.sources(MyBlogApp.class);
    }

}
