package com.atguigu.gmall.manager.es;

import lombok.Data;

import java.io.Serializable;

/**
 * sku平台属性在es中保存的信息
 */
@Data
public class SkuBaseAttrEsVo implements Serializable{
    Integer valueId;
}
