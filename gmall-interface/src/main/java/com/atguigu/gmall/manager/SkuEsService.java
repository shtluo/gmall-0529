package com.atguigu.gmall.manager;

import com.atguigu.gmall.manager.es.SkuSearchParamEsVo;
import com.atguigu.gmall.manager.es.SkuSearchResultEsVo;

public interface SkuEsService {
    /**
     * 商品上架操作
     * @param skuId
     */
    void onSale(Integer skuId);

    SkuSearchResultEsVo searchSkuFromES(SkuSearchParamEsVo paramEsVo);

    /**
     * 更新商品中ES的热度值
     * @param skuId
     * @param hincrBy
     */
    void updateHotScore(Integer skuId, Long hincrBy);
}
