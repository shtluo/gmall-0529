package com.atguigu.gmall.manager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/attr")
@Controller
public class ManagerController {
    /**
     * 去属性平台页面
     * @return
     */
    @RequestMapping("/attrListPage.html")
    public String toAttrListPage(){
        return "attr/attrListPage";

    }
}
