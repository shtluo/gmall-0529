package com.atguigu.gmall.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.manager.BaseAttrInfo;
import com.atguigu.gmall.manager.SkuEsService;
import com.atguigu.gmall.manager.SkuService;
import com.atguigu.gmall.manager.SpuInfoService;
import com.atguigu.gmall.manager.sku.SkuInfo;
import com.atguigu.gmall.manager.spu.SpuImage;
import com.atguigu.gmall.manager.spu.SpuSaleAttr;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/sku")
@RestController
public class SkuController {
    @Reference
    SkuService skuService;
    @Reference
    SpuInfoService spuInfoService;
    @Reference
    SkuEsService skuEsService;

    @ResponseBody
    @RequestMapping("/onsale")
    public String onSale(@RequestParam("skuId") Integer skuId){
        //这个方法最好是一个异步方法，不要阻塞其他请求了
        skuEsService.onSale(skuId);
        return "ok";
    }



    @RequestMapping("/skuinfo")
    public  List<SkuInfo> getSkuInfoBySpuId(@RequestParam("id") Integer spuId){
        return skuService.getSkuInfoBySpuId(spuId);
    }




    /**
     * sku大保存
     * @param skuInfo
     * @return
     */
    @RequestMapping("/bigSave")
    public String skuBigSave(@RequestBody SkuInfo skuInfo){
        log.info("skuInfo数据:{}",skuInfo);
        skuService.getSkuBigSave(skuInfo);
        return "ok";
    }

    /**
     * 查询所有的spu图片
     * @param spuId
     * @return
     */
    @RequestMapping("/spuImages")
    public List<SpuImage> getSpuImages(@RequestParam("id") Integer spuId){
        return spuInfoService.getSpuImages(spuId);
    }

    /**
     * 按照三级分类查出他下面的所有平台属性以及他的值
     * @param catalog3id
     * @return
     */
    @RequestMapping("/base_attr_info.json")
    public List<BaseAttrInfo> getBaseAttrInfoByCatalog3Id(@RequestParam("id") Integer catalog3id){
        return skuService.getBaseAttrInfoByCatalog3Id(catalog3id);
    }
    @RequestMapping("/spu_sale_attr.json")
    public List<SpuSaleAttr> getSpuSaleAttr(@RequestParam("id") Integer spuId){
        return skuService.getSpuSaleAttrBySpuId(spuId);
    }
}
