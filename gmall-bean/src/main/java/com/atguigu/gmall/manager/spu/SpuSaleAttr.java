package com.atguigu.gmall.manager.spu;

import com.atguigu.gmall.SuperBean;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.List;

/**
 * Spu销售属性
 * {
 *     id,spuId,saleAttrId,saleAttrName,
 *     saleAttrValues[
 *       {id,saleAttrValueName},
 *       {id,saleAttrValueName}
 *     ]
 * }
 */
@Data
public class SpuSaleAttr extends SuperBean {

    //spu_id  sale_attr_id  sale_attr_name  saleAttrValues
    private Integer spuId;
    private Integer saleAttrId;
    private String saleAttrName;
    @TableField(exist = false)
    private List<SpuSaleAttrValue> saleAttrValues;
}
