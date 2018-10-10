package com.atguigu.gmall.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.manager.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 这个controller来给easyui提供url提供访问，返回json数据
 */
@RequestMapping("/basecatalog")
@RestController
public class BaseCatalogRestController {
    @Reference //这是一个远程接口
    CatalogService catalogService;
    @Reference
    BaseAttrInfoService baseAttrInfoService;

    @RequestMapping("/attrs.json")
    public List<BaseAttrInfo> getBaseAttrInfoByCatalog3Id(Integer id){
        return baseAttrInfoService.getBaseAttrInfoByCatalog3Id(id);
    }

    @RequestMapping("/1/list.json")
    public List<BaseCatalog1> listBasecatalog1(){
        return catalogService.getAllBaseCatalog1();
    }
    @RequestMapping("/2/list.json")
    public List<BaseCatalog2> listBasecatalog2(Integer id){
        return catalogService.getBaseCatalog2ByC1Id(id);
    }
    @RequestMapping("/3/list.json")
    public List<BaseCatalog3> listBasecatalog3(Integer id){
        return catalogService.getBaseCatalog3ByC2Id(id);
    }
}
