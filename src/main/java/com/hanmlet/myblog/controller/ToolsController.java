package com.hanmlet.myblog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * FileName : ToolsController
 *
 * @author : hanml
 * @date : 2018/12/18 11:38
 * Description :
 * Copyright (C), 2015-2018, asiaInfo
 */
@RequestMapping("tools")
@Controller
public class ToolsController {

    @GetMapping("init")
    public String init(){
        return  "tools";
    }
}
