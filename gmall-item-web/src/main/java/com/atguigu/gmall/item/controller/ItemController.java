package com.atguigu.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.annotation.LoginRequired;
import com.atguigu.gmall.manager.SkuService;
import com.atguigu.gmall.manager.sku.SkuAttrValueMappingTo;
import com.atguigu.gmall.manager.sku.SkuInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class ItemController {
    @Reference
    SkuService skuService;

    /**
     * 我们可以写一个拦截器，在请求达到目标方法的时候，看看方法是否需要登录才访问，如果需要就进行登录操作
     * @return
     */
    @LoginRequired
    @RequestMapping("/haha")
    public String haha(HttpServletRequest request){
        Map<String,Object> userInfo = (Map<String, Object>) request.getAttribute("userInfo");


        //要用户名
        //1、如果这个token不是没有意义的随机数（只用来作呕redis中的标识）
        //2、假设这个token是有意义的
        //3、这串数字已经包含了你的常用信息，你要用的这些常用信息的时候就不用查了
        //不可伪造
        //JWT（json web token）
        // UserInfo = redis.get(token);
        log.info("解码到的用户信息：{}",userInfo);
        return "haha";
    }



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
        //3、增加点击率，更新ES中的hotScore的值
        //redis 把redis中的商品热度保存起来增加即可
        skuService.incrSkuHotScore(skuId);
        return "item";
    }

}
