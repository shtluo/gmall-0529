package com.atguigu.gmall.manager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HelloController {

    @RequestMapping("/main")
    public String hello(){
        //org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties
        // public static final String DEFAULT_PREFIX = "classpath:/templates/";

        // public static final String DEFAULT_SUFFIX = ".html";

        return "main";
    }
}
