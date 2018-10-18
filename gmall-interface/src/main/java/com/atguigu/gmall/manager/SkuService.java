package com.atguigu.gmall.manager;

import com.atguigu.gmall.manager.es.SkuBaseAttrEsVo;
import com.atguigu.gmall.manager.sku.SkuAttrValueMappingTo;
import com.atguigu.gmall.manager.sku.SkuInfo;
import com.atguigu.gmall.manager.spu.SpuSaleAttr;

import java.util.List;

public interface SkuService {

    public List<BaseAttrInfo> getBaseAttrInfoByCatalog3Id(Integer catalog3id);

    public List<SpuSaleAttr> getSpuSaleAttrBySpuId(Integer spuId);

    void getSkuBigSave(SkuInfo skuInfo);

    List<SkuInfo> getSkuInfoBySpuId(Integer spuId);

    /**
     * 查询某个sku的详细信息
     * @param skuId
     * @return
     */
    SkuInfo getSkuInfoBySkuId(Integer skuId) throws InterruptedException, Exception;

    List<SkuAttrValueMappingTo> getSkuAttrValueMapping(Integer spuId);

    List<SkuBaseAttrEsVo> getSkuBaseAttrValueId(Integer skuId);
}
