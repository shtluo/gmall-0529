package com.atguigu.gmall.manager.es;

import lombok.Data;

import java.io.Serializable;

/**
 * 页面传入的所有参数
 */
@Data
public class SkuSearchParamEsVo implements Serializable {
    String keyword; //关键词搜索
    Integer catalog3Id; //三级分类Id
    Integer[] valueId; //属性值id
    Integer pageNo = 1;
    Integer pageSize = 12;
    String sortField = "hotScore";
    String sortOrder = "desc";
}
