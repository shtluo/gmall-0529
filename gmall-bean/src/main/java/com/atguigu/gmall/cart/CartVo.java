package com.atguigu.gmall.cart;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Setter
public class CartVo implements Serializable{
    @Getter
    List<CartItem> cartItems; //就是购物项的总和
    private BigDecimal totalPrice; //整个购物车的总价

    public BigDecimal getTotalPrice() {
        totalPrice = new BigDecimal(0);
        if(cartItems!=null && cartItems.size()>0){
            for (CartItem cartItem : cartItems) {
                totalPrice = totalPrice.add(cartItem.getTotalPrice());
            }

        }
        return totalPrice;
    }
}
