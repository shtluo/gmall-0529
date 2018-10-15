package com.atguigu.gmall.manager.spu;

import com.atguigu.gmall.SuperBean;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.List;

/**
 * Spu信息
 */
@Data
public class SpuInfo extends SuperBean {
    //spu_name  description  catalog3_id   tm_id
    private String spuName;//商品名字
    private String description;//描述
    private Integer catalog3Id;//三级分类id
    private Integer tmId; //品牌id

    @TableField(exist = false)
    private List<SpuImage> spuImages; //spu_id  img_name  img_url
    @TableField(exist = false)
    private List<SpuSaleAttr> spuSaleAttrs;  //多级封装spu_id  sale_attr_id  sale_attr_name  saleAttrValues
}
