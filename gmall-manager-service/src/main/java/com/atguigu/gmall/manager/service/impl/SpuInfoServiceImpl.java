package com.atguigu.gmall.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.manager.SpuInfoService;
import com.atguigu.gmall.manager.mapper.spu.*;
import com.atguigu.gmall.manager.spu.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
@Service
public class SpuInfoServiceImpl implements SpuInfoService {

    @Autowired
    SpuInfoMapper spuInfoMapper;
    @Autowired
    SpuImageMapper spuImageMapper;
    @Autowired
    SpuSaleAttrMapper spuSaleAttrMapper;
    @Autowired
    SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    @Autowired
    BaseSaleAttrMapper baseSaleAttrMapper;

    @Override
    public List<SpuInfo> getSpuInfoByCatalog3Id(Integer catalog3id) {
        List<SpuInfo> catalog3_id = spuInfoMapper.selectList(new QueryWrapper<SpuInfo>()
                .eq("catalog3_id", catalog3id));
        return catalog3_id;
    }

    @Override
    public List<BaseSaleAttr> getBaseSaleAttr() {
        List<BaseSaleAttr> baseSaleAttrs = baseSaleAttrMapper.selectList(null);
        return baseSaleAttrs;
    }

    @Override
    public void saveBigSpuInfo(SpuInfo spuInfo) {
        //1、保存基本信息
        spuInfoMapper.insert(spuInfo);
        //获取到刚才保存的spu的id
        Integer spuId = spuInfo.getId();

        //2、保存spu的图片信息
        List<SpuImage> spuImages = spuInfo.getSpuImages();
        for (SpuImage spuImage : spuImages) {
            //设置好商品id
            spuImage.setSpuId(spuId);
            spuImageMapper.insert(spuImage);
        }
        //3、保存商品属性信息
        List<SpuSaleAttr> spuSaleAttrs = spuInfo.getSpuSaleAttrs();
        for (SpuSaleAttr spuSaleAttr : spuSaleAttrs) {
            spuSaleAttr.setSpuId(spuId);
            spuSaleAttrMapper.insert(spuSaleAttr);

            //4、保存销售属性值的信息
            List<SpuSaleAttrValue> saleAttrValues = spuSaleAttr.getSaleAttrValues();
            for (SpuSaleAttrValue saleAttrValue : saleAttrValues) {
                //设置spu的id
                saleAttrValue.setSpuId(spuId);
                //获取到销售属性id
                saleAttrValue.setSaleAttrId(spuSaleAttr.getSaleAttrId());
                spuSaleAttrValueMapper.insert(saleAttrValue);
            }


        }

    }

    @Override
    public List<SpuImage> getSpuImages(Integer spuId) {
        return spuImageMapper.selectList(new QueryWrapper<SpuImage>().eq("spu_id",spuId));
    }


}
