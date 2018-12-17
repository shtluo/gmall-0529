package com.atguigu.gmall.cart;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 购物车中每一项数据
 */
@Setter
public class CartItem implements Serializable{
    @Getter
    private SkuItem skuItem; //当前这个购物项的的详情
    @Getter
    private Integer num; //当前项的数量

    private BigDecimal totalPrice; //当前项的总价

    @Setter
    @Getter
    private boolean isCheck = false; //可以发Ajax请求更新此字段


    public BigDecimal getTotalPrice(){
        BigDecimal multiply = skuItem.getPrice().multiply(new BigDecimal(num));
        this.totalPrice=multiply;
        return multiply;
    }
}
