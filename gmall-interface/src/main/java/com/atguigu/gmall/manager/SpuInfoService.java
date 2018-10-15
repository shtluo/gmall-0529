package com.atguigu.gmall.manager;

import com.atguigu.gmall.manager.spu.BaseSaleAttr;
import com.atguigu.gmall.manager.spu.SpuInfo;

import java.util.List;

public interface SpuInfoService {
    public List<SpuInfo> getSpuInfoByCatalog3Id(Integer catalog3id);


    List<BaseSaleAttr> getBaseSaleAttr();

    //spu的大保存
    public void saveBigSpuInfo(SpuInfo spuInfo);
}
