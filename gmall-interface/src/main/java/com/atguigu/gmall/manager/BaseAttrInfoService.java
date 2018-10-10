package com.atguigu.gmall.manager;

import java.util.List;

/**
 * 平台属性
 */
public interface BaseAttrInfoService {
    /**
     * 获取三级分类下的平台属性名
     * @param Catalog3Id
     * @return
     */
    public List<BaseAttrInfo> getBaseAttrInfoByCatalog3Id(Integer Catalog3Id);

    /**
     * 根据平台属性名获取平台属性值
     * @param AttrId
     * @return
     */
    public List<BaseAttrValue> getBaseAttrValueByAttrId(Integer AttrId);
}
