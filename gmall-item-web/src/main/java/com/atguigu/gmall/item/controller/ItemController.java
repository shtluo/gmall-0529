package com.atguigu.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.manager.SkuEsService;
import com.atguigu.gmall.manager.SkuService;
import com.atguigu.gmall.manager.sku.SkuAttrValueMappingTo;
import com.atguigu.gmall.manager.sku.SkuInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Controller
public class ItemController {
    @Reference
    SkuService skuService;

    @RequestMapping("/{skuId}.html")
    public String itemPage(@PathVariable("skuId") Integer skuId, Model model, HttpServletRequest request){
        //1、查出sku的详细信息
        SkuInfo skuInfo = null;
        try {
            skuInfo = skuService.getSkuInfoBySkuId(skuId);
            if(skuInfo == null){
                return "skuInfoError";
            }
        } catch (Exception e) {
            log.info("查skuInfo的详细信息失败");
        }
        model.addAttribute("skuInfo",skuInfo);

        //2、查出当前sku对应的spu下面的所有属性值组合
        /*    id  spu_id  sku_name    sale_attr_value_id  sale_attr_value_name
        ------  ------  ----------  ------------------  ----------------------
              30      53  vivoX9Plus  101,99              V2.0,黑色           */
        Integer spuId = skuInfo.getSpuId();
        List<SkuAttrValueMappingTo> valueMappingTos = skuService.getSkuAttrValueMapping(spuId);
        log.info("valueMappingTos{}:",valueMappingTos);
        model.addAttribute("skuValueMapping",valueMappingTos);
        return "item";
    }

}
