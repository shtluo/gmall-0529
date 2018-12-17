package com.atguigu.gmall.search.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.manager.BaseAttrInfo;
import com.atguigu.gmall.manager.SkuEsService;
import com.atguigu.gmall.manager.SkuService;
import com.atguigu.gmall.manager.es.SkuBaseAttrEsVo;
import com.atguigu.gmall.manager.es.SkuInfoEsVo;
import com.atguigu.gmall.manager.es.SkuSearchParamEsVo;
import com.atguigu.gmall.manager.es.SkuSearchResultEsVo;
import com.atguigu.gmall.manager.sku.SkuInfo;
import com.atguigu.gmall.search.constant.EsConstant;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.Update;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class SkuEsServiceImpl implements SkuEsService {
    @Reference
    SkuService skuService;
    @Autowired
    JestClient jestClient;

    @Async //表示这是一个异步调用
    @Override
    public void onSale(Integer skuId) {
        try {
            //1、获取sku的详细信息
            SkuInfo info = skuService.getSkuInfoBySkuId(skuId);
            log.info("商品上架获取详细的sku信息：{}",info);

            SkuInfoEsVo skuInfoEsVo = new SkuInfoEsVo();
            //将查询出的值拷贝过来
            BeanUtils.copyProperties(info,skuInfoEsVo);
            //查询出的所有平台属性的值
            List<SkuBaseAttrEsVo> vos = skuService.getSkuBaseAttrValueId(skuId);
            skuInfoEsVo.setBaseAttrEsVos(vos);

            //保存sku信息到es
            Index index = new Index.Builder(skuInfoEsVo)
                    .index(EsConstant.GMALL_INDEX)
                    .type(EsConstant.GMALL_SKU_TYPE)
                    .id(skuInfoEsVo.getId() + "").build();
            try {
                jestClient.execute(index);
            }catch (Exception e){
                log.error("ES保存出问题");
            }


            String jsonString = JSON.toJSONString(info);
            log.info("查到的json数据：{}",jsonString);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 按照查询参数查询出页面需要的数据
     * @param paramEsVo
     * @return
     */
    @Override
    public SkuSearchResultEsVo searchSkuFromES(SkuSearchParamEsVo paramEsVo) {
        SkuSearchResultEsVo resultEsVo = null;
        //1、传入dsl语句
        //dls的大拼串
        String queryDsl = buildSearchQueryDsl(paramEsVo);
        Search build = new Search.Builder(queryDsl).addIndex(EsConstant.GMALL_INDEX)
                .addType(EsConstant.GMALL_SKU_TYPE)
                .build();
        //2、执行查询
        try {
            SearchResult result = jestClient.execute(build);

            //3、把查询出来的result处理成能给页面返回的SkuSearchResultEsVo
            resultEsVo = buildSkuSearchResult(result);
            resultEsVo.setPageNo(paramEsVo.getPageNo());
            return resultEsVo;
        } catch (IOException e) {
            log.error("ES查询出了故障：{}",e);
        }
        return resultEsVo;
    }

    /**
     * 更新商品的热度值
     * @param skuId
     * @param hincrBy
     */
    @Async
    @Override
    public void updateHotScore(Integer skuId, Long hincrBy) {
        String updateHotScore = "{\"doc\": {\"hotScore\":"+hincrBy+"}}";
        Update update = new Update.Builder(updateHotScore).index(EsConstant.GMALL_INDEX)
                .type(EsConstant.GMALL_SKU_TYPE).id(skuId+"").build();
        try {
            jestClient.execute(update);
        } catch (IOException e) {
            log.error("SkuEsServiceImpl()更新商品的热度值异常：{}",e);
        }
    }

    //构造dsl语句
    public String buildSearchQueryDsl(SkuSearchParamEsVo paramEsVo){
        //创建一个能帮我们搜索数据的构造器，来构建dsl
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //过滤
        if(paramEsVo.getCatalog3Id() != null){
            //过滤三级分类信息
            TermQueryBuilder QueryCatalog3Id= new TermQueryBuilder("catalog3Id",paramEsVo.getCatalog3Id());
            boolQuery.filter(QueryCatalog3Id);
        }
        if(paramEsVo.getValueId() != null && paramEsVo.getValueId().length > 0){
            //过滤属性值id
            for (Integer vid : paramEsVo.getValueId()) {
                TermQueryBuilder termQueryValueId = new TermQueryBuilder("baseAttrEsVos.valueId", vid);
                boolQuery.filter(termQueryValueId);
            }


        }

        //搜索
        if(!StringUtils.isEmpty(paramEsVo.getKeyword())){
            boolQuery.must(new MatchQueryBuilder("skuName",paramEsVo.getKeyword()));

            //有搜索才有高亮
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuName");
            highlightBuilder.preTags("<span style='color:red'>");
            highlightBuilder.postTags("</span>");
            searchSourceBuilder.highlight(highlightBuilder);
        }

        //以上查询与过滤完成
        searchSourceBuilder.query(boolQuery);

        //排序
        if(!StringUtils.isEmpty(paramEsVo.getSortField())){
            SortOrder sortOrder = null;
            switch (paramEsVo.getSortOrder()){
                case "desc":sortOrder = SortOrder.DESC ; break;
                case "asc":sortOrder = SortOrder.ASC; break;
                default:sortOrder = SortOrder.DESC;
            }
            searchSourceBuilder.sort(paramEsVo.getSortField(), sortOrder);
        }

        //分页
        searchSourceBuilder.from((paramEsVo.getPageNo() - 1) * paramEsVo.getPageSize());
        searchSourceBuilder.size(paramEsVo.getPageSize());

        //聚合
        TermsBuilder termsBuilder = new TermsBuilder("valueIdAggs");
        termsBuilder.field("baseAttrEsVos.valueId");
        searchSourceBuilder.aggregation(termsBuilder);



        //它的toString()就是用来获取dsl
        String dsl = searchSourceBuilder.toString();
        return dsl;
    }

    //将查询的结果构造成页面能用的数据
    private SkuSearchResultEsVo buildSkuSearchResult(SearchResult result){
        SkuSearchResultEsVo resultEsVo = new SkuSearchResultEsVo();
        //所有skuInfo的集合
        List<SkuInfoEsVo> skuInfoEsVoList = null;
        //1、从es搜索的结果中找到所有的skuinfo信息

        //拿到命中的所有记录
        List<SearchResult.Hit<SkuInfoEsVo, Void>> hits = result.getHits(SkuInfoEsVo.class);
        if(hits == null || hits.size() == 0){
            return null;
        }else{
            //查到了数据 【hits.size()防止集合扩容带来性能影响】
             skuInfoEsVoList = new ArrayList<>(hits.size());
            for (SearchResult.Hit<SkuInfoEsVo, Void> hit : hits) {
                SkuInfoEsVo source = hit.source;

                //页面有可能有高亮
                Map<String, List<String>> highlight = hit.highlight;
                //普通非全文模糊【匹配的是没有高亮的】
                if(highlight != null){
                    String highText = highlight.get("skuName").get(0);
                    //把文本替换为高亮
                    source.setSkuName(highText);
                }
                skuInfoEsVoList.add(source);
            }
        }
        //只保存了skuInfo的数据
        resultEsVo.setSkuInfoEsVos(skuInfoEsVoList);
        //总记录数
        resultEsVo.setTotal(result.getTotal().intValue());
        //从聚合的数据中取出所欲平台属性以及它的值
        List<BaseAttrInfo> baseAttrInfos = getBaseAttrInfoGroupByValueId(result);

        resultEsVo.setBaseAttrInfos(baseAttrInfos);
        return resultEsVo;
    }

    /**
     * 根据ES聚合的数据找到所有平台属性的的值
     * @param result
     * @return
     */
    private List<BaseAttrInfo> getBaseAttrInfoGroupByValueId(SearchResult result){
        MetricAggregation aggregations = result.getAggregations();
        //获取term聚合出来的数据
        TermsAggregation termsAggregation = aggregations.getTermsAggregation("valueIdAggs");
        List<TermsAggregation.Entry> buckets = termsAggregation.getBuckets();
        List<Integer> valueIds = new ArrayList<>();
        //遍历buckets
        for (TermsAggregation.Entry bucket : buckets) {
            String key = bucket.getKey();
            valueIds.add(Integer.parseInt(key));
        }
        //查询出所有的平台属性值
        return skuService.getBaseAttrInfoGroupByValueId(valueIds);
    }
}
