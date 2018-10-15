package com.atguigu.gmall.manager;

import com.atguigu.gmall.manager.spu.SpuSaleAttr;

import java.util.List;

public interface SkuService {

    public List<BaseAttrInfo> getBaseAttrInfoByCatalog3Id(Integer catalog3id);

    public List<SpuSaleAttr> getSpuSaleAttrBySpuId(Integer spuId);
}
