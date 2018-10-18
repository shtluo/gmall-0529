package com.atguigu.gmall.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.manager.BaseAttrInfo;
import com.atguigu.gmall.manager.SkuService;
import com.atguigu.gmall.manager.constant.RedisCacheKeyConst;
import com.atguigu.gmall.manager.es.SkuBaseAttrEsVo;
import com.atguigu.gmall.manager.mapper.BaseAttrInfoMapper;
import com.atguigu.gmall.manager.mapper.sku.SkuAttrValueMapper;
import com.atguigu.gmall.manager.mapper.sku.SkuImageMapper;
import com.atguigu.gmall.manager.mapper.sku.SkuInfoMapper;
import com.atguigu.gmall.manager.mapper.sku.SkuSaleAttrValueMapper;
import com.atguigu.gmall.manager.mapper.spu.SpuSaleAttrMapper;
import com.atguigu.gmall.manager.sku.*;
import com.atguigu.gmall.manager.spu.SpuSaleAttr;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    SpuSaleAttrMapper spuSaleAttrMapper;
    @Autowired
    SkuInfoMapper skuInfoMapper;
    @Autowired
    SkuImageMapper skuImageMapper;
    @Autowired
    SkuAttrValueMapper skuAttrValueMapper;
    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Autowired
    JedisPool jedisPool;

    @Override
    public List<BaseAttrInfo> getBaseAttrInfoByCatalog3Id(Integer catalog3id) {
        return baseAttrInfoMapper.getBaseAttrInfoByCatalog3Id(catalog3id);
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrBySpuId(Integer spuId) {
        return spuSaleAttrMapper.getSpuSaleAttrBySpuId(spuId);
    }


    @Override
    public void getSkuBigSave(SkuInfo skuInfo) {
        //1、先保存基本的sku信息
        skuInfoMapper.insert(skuInfo);
        //2、再保存提交的图片、平台属性、销售属性等
        List<SkuImage> skuImages = skuInfo.getSkuImages();
        for (SkuImage skuImage : skuImages) {
            skuImage.setSkuId(skuInfo.getId());
            skuImageMapper.insert(skuImage);
        }


        List<SkuAttrValue> skuAttrValues = skuInfo.getSkuAttrValues();
        for (SkuAttrValue skuAttrValue : skuAttrValues) {
            skuAttrValue.setSkuId(skuInfo.getId());
            skuAttrValueMapper.insert(skuAttrValue);
        }


        List<SkuSaleAttrValue> skuSaleAttrValues = skuInfo.getSkuSaleAttrValues();
        for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValues) {
            skuSaleAttrValue.setSkuId(skuInfo.getId());
            skuSaleAttrValueMapper.insert(skuSaleAttrValue);
        }

    }


    @Override
    public List<SkuInfo> getSkuInfoBySpuId(Integer spuId) {
        return skuInfoMapper.selectList(new QueryWrapper<SkuInfo>().eq("spu_id",spuId));
    }

    @Override
        public SkuInfo getSkuInfoBySkuId(Integer skuId) throws Exception {
        Jedis jedis = jedisPool.getResource();
        String key = RedisCacheKeyConst.SKU_INFO_PREFIX+skuId+RedisCacheKeyConst.SKU_INFO_SUFFIX;
        SkuInfo result = null;
        //先去缓存中看看
        //数据肯定不能一直在缓存中，需要设置缓存时间
        String s = jedis.get(key);
        if(s !=null){
            //缓存中有转成我们想要的对象
            log.info("缓存中找到了。。。。");
            result = JSON.parseObject(s,SkuInfo.class);
            jedis.close();
        }
        if("null".equals(s)){
            //用来防止缓存穿透的
            //之前数据库查过，但是没有，所以给缓存中存了一个"null"字符串
            return null;
        }
        if(s == null){
            //缓存中没有先从数据库查，然后放到缓存中
            //我们需要加锁
            //拿到锁再去数据库查
            String token = UUID.randomUUID().toString();
            String lock = jedis.set(RedisCacheKeyConst.LOCK_SKU_INFO+skuId, token, "NX", "EX", RedisCacheKeyConst.LOCK_TIMEOUT);
            if(lock == null){
                log.info("没有拿到锁等待一会儿重试");
                //没有拿到锁等待一会儿重试
                Thread.sleep(3000);
                //自旋锁
                getSkuInfoBySkuId(skuId);
            }else if("OK".equals(lock)){
                log.info("获取到锁，查数据库");
                result =getFromDb(skuId);
                //redis的key
                String skuInfoJson = JSON.toJSONString(result);
                if("null".equals(skuInfoJson)){
                    //空数据缓存时间短
                    jedis.setex(key,RedisCacheKeyConst.SKU_INFO_NULL_TIMEOUT,skuInfoJson);
                }else{
                    //空数据缓存时间长
                    jedis.setex(key,RedisCacheKeyConst.SKU_INFO_TIMEOUT,skuInfoJson);
                }
                log.info("从数据库中查到的数据{}",skuInfoJson);
                //String redisToken = jedis.get(RedisCacheKeyConst.LOCK_SKU_INFO+skuId);
                //错误的删除锁
                /*if(token.equals(redisToken)){
                    //手动释放锁,即使释放失败，也会自动删除
                    jedis.del(RedisCacheKeyConst.LOCK_SKU_INFO);
                }else{
                    log.info("设置锁和释放锁的令牌不一致，什么都不用做");
                }*/

                //脚本；正确的解锁；一定要是原子操作
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                jedis.eval(script,
                        Collections.singletonList(RedisCacheKeyConst.LOCK_SKU_INFO+skuId),
                        Collections.singletonList(token));

            }
        }

        return result;
    }

    @Override
    public List<SkuAttrValueMappingTo> getSkuAttrValueMapping(Integer spuId) {
        return skuInfoMapper.getSkuAttrValueMapping(spuId);
    }

    @Override
    public List<SkuBaseAttrEsVo> getSkuBaseAttrValueId(Integer skuId) {
        List<SkuAttrValue> skuAttrValues = skuAttrValueMapper.selectList(new QueryWrapper<SkuAttrValue>()
                .eq("sku_id", skuId));
        List<SkuBaseAttrEsVo> result = new ArrayList<>();
        for (SkuAttrValue skuAttrValue : skuAttrValues) {
            Integer valueId = skuAttrValue.getId();
            SkuBaseAttrEsVo vo = new SkuBaseAttrEsVo();
            vo.setValueId(valueId);
            result.add(vo);
        }

        return result;
    }


    private SkuInfo getFromDb(Integer skuId){
        log.info("缓存中没有先从数据库查，然后放到缓存中：{}",skuId);
        //1、先查出skuInfo基本信息
        //service应该使用缓存机制
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if(skuInfo == null){
            //即使没有数据也放到缓存中，返回回去，结局nullpointException
            return null;
        }
        //2、查出skuInfo所有图片信息
        List<SkuImage> skuImages = skuImageMapper.selectList(new QueryWrapper<SkuImage>()
                .eq("sku_id", skuInfo.getId()));
        skuInfo.setSkuImages(skuImages);
        //3、查出整个skuAttrValue信息
        List<SkuAllSaveAttrAndValueTo> skuAllSaveAttrAndValueTos =
                skuImageMapper.getSkuAllSaveAttrAndValue(skuInfo.getId(),skuInfo.getSpuId());
        skuInfo.setSkuAllSaveAttrAndValueTos(skuAllSaveAttrAndValueTos);
        //4、添加缓存 :redis作为缓存中间件；内存数据库
        return skuInfo;
    }


}
