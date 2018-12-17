package com.atguigu.gmall.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.annotation.LoginRequired;
import com.atguigu.gmall.manager.SkuEsService;
import com.atguigu.gmall.manager.es.SkuSearchParamEsVo;
import com.atguigu.gmall.manager.es.SkuSearchResultEsVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ListController {

    @Reference
    SkuEsService skuEsService;

    @LoginRequired
    @RequestMapping("/hehe")
    public String hehe(){
        //想要用户数据，只需要要在拦截器解码，然后来到这个方法
        return "hehe";
    }
    /**
     * 将页面提交的所有数据提交入参
     * @param paramEsVo
     * @return
     */
    @RequestMapping("/list.html")
    public String list(SkuSearchParamEsVo paramEsVo, Model model){
        //搜索完成后返回这个对象，这个对象里面有所有数据
        SkuSearchResultEsVo searchResult = skuEsService.searchSkuFromES(paramEsVo);
        model.addAttribute("searchResult",searchResult);
        return "list";
    }

}
