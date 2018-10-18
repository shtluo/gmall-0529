package com.atguigu.gmall.manager;

public interface SkuEsService {
    /**
     * 商品上架操作
     * @param skuId
     */
    void onSale(Integer skuId);
}
