package com.atguigu.gmall.manager.es;

import com.atguigu.gmall.manager.BaseAttrInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 页面需要显示的所有数据
 */
@Data
public class SkuSearchResultEsVo implements Serializable{
    //1、搜索的skuInfo结果
    private List<SkuInfoEsVo> skuInfoEsVos;
    //2、分页条上该显示的内容
    private Integer total; //总记录条数
    private Integer pageNo; //当前是第几页数据

    //3、告诉页面可供筛选的所有平台属性名和属性名对应的每一个值
    private List<BaseAttrInfo> baseAttrInfos;
}
