package com.atguigu.gmall.manager;

import com.atguigu.gmall.SuperBean;
import lombok.Data;


@Data
public class BaseAttrValue extends SuperBean {

    private String valueName;

    private Integer attrId;

   // private String isEnabled;

}
