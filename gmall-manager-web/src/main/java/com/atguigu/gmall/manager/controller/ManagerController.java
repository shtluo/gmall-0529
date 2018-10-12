package com.atguigu.gmall.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.manager.BaseAttrInfo;
import com.atguigu.gmall.manager.BaseAttrInfoService;
import com.atguigu.gmall.manager.BaseAttrValue;
import com.atguigu.gmall.manager.vo.BaseAttrInfoAndValueVO;
import com.atguigu.gmall.manager.vo.BaseAttrValueVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequestMapping("/attr")
@Controller
public class ManagerController {

    @Reference
    BaseAttrInfoService baseAttrInfoService;

    /**
     * @RequestBody springmvc的功能：将请求体中的数据封装成指定的对象BaseAttrInfoAndValueVO
     * 1、如果请求体中是json字符串，直接可以将json转成对象
     * 2、页面如何 提交原生的json  使$.ajax();
     *
     * @param valueVO
     * @return
     */
    @ResponseBody
    @RequestMapping("/updates")
    public String saveOrUpdateOrDeleteAttrInfoAndValue(@RequestBody BaseAttrInfoAndValueVO valueVO){
        log.info("页面提交来的数据:  {}:",valueVO);
        //1.判断修改还是添加
        if(valueVO.getId() != null){
            //1、修改基本属性名
            //2、修改这个属性对应的所有值
            BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
            //将vo中的所有属性复制到bean中
            BeanUtils.copyProperties(valueVO,baseAttrInfo);

            ArrayList<BaseAttrValue> baseAttrValues = new ArrayList<>();
            for (BaseAttrValueVO vo : valueVO.getAttrValues()) {
                BaseAttrValue baseAttrValue = new BaseAttrValue();
                BeanUtils.copyProperties(vo,baseAttrValue);
                baseAttrValues.add(baseAttrValue);
            }
            baseAttrInfo.setAttrValues(baseAttrValues);

            baseAttrInfoService.saveOrUpdateBaseInfo(baseAttrInfo);
        }else{
            //添加
            BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
            baseAttrInfo.setAttrName(valueVO.getAttrName());
            baseAttrInfo.setCatalog3Id(valueVO.getCatalog3Id());

            baseAttrInfoService.saveOrUpdateBaseInfo(baseAttrInfo);

        }
        return "ok";
    }




    /**
     * 返回表格属性值 对应数据库`base_attr_value`
     *[
     {
     "id": 1,
     "valueName": "数学人教版",
     "attrId": 1
     },
     {
     "id": 2,
     "valueName": "英语人教版",
     "attrId": 1
     }
     ]
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping("/value/{id}")
    public List<BaseAttrValue> getBaseAttrValueByAttrId(@PathVariable("id") Integer id){
        return baseAttrInfoService.getBaseAttrValueByAttrId(id);
    }
    /**
     * 去属性平台页面
     * @return
     */
    @RequestMapping("/attrListPage.html")
    public String toAttrListPage(){
        return "attr/attrListPage";

    }
}
