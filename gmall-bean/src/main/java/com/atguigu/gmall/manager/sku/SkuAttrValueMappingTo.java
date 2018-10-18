package com.atguigu.gmall.manager.sku;

import lombok.Data;

import java.io.Serializable;

@Data
public class SkuAttrValueMappingTo implements Serializable {
    //    id  spu_id  sku_name    sale_attr_value_id_mapping  sale_attr_value_name_mapping
    private Integer skuId;
    private Integer spuId;
    private String skuName;
    private String saleAttrValueIdMapping;
    private String saleAttrValueNameMapping;

}
