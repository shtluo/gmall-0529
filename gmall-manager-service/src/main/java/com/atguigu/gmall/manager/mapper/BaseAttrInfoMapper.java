package com.atguigu.gmall.manager.mapper;

import com.atguigu.gmall.manager.BaseAttrInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo>{

    /**
     * 按照三级分类查出平台属性的名和值
     * @param catalog3id
     * @return
     */
    List<BaseAttrInfo> getBaseAttrInfoByCatalog3Id(Integer catalog3id);
}
