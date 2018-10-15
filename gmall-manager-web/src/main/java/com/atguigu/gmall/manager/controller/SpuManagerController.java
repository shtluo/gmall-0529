package com.atguigu.gmall.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.manager.SpuInfoService;
import com.atguigu.gmall.manager.spu.BaseSaleAttr;
import com.atguigu.gmall.manager.spu.SpuInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.websocket.server.PathParam;
import java.util.List;

@Slf4j
@RequestMapping("/spu")
@Controller
public class SpuManagerController {

    @Reference
    SpuInfoService spuInfoService;

    @ResponseBody
    @RequestMapping("/bigSave")
    public String saveAllSpuInfos(@RequestBody SpuInfo spuInfo){
        log.info("SpuInfo大对象的数据{}：",spuInfo);
        spuInfoService.saveBigSpuInfo(spuInfo);
        return "ok";
    }

    @ResponseBody
    @RequestMapping("/base_sale_attr")
    public List<BaseSaleAttr> getBaseSaleAttr(){
        return spuInfoService.getBaseSaleAttr();

    }


    @ResponseBody
    @RequestMapping("/info.json")
    public List<SpuInfo> getSpuInfoByCatalog3Id(@RequestParam("catalog3id") Integer catalog3id){
        return spuInfoService.getSpuInfoByCatalog3Id(catalog3id);
    }

    @RequestMapping("/listPage.html")
    public String listPage(){
        return "spu/spuListPage";
    }
}
