package com.atguigu.gmall.search;

import com.atguigu.gmall.manager.es.SkuSearchParamEsVo;
import com.atguigu.gmall.search.service.impl.SkuEsServiceImpl;
import org.junit.Test;

import java.util.Scanner;

public class SimpleTest {
    @Test
    public void esTest(){
        SkuEsServiceImpl skuEsService = new SkuEsServiceImpl();
        SkuSearchParamEsVo paramEsVo = new SkuSearchParamEsVo();
        paramEsVo.setPageNo(1);
        paramEsVo.setValueId(new Integer[]{55,56});
        paramEsVo.setCatalog3Id(61);
        paramEsVo.setKeyword("小米8");
        paramEsVo.setSortOrder("desc");
        String s = skuEsService.buildSearchQueryDsl(paramEsVo);
        System.out.println(s);
    }

    @Test
    public void sort(){
        Scanner input = new Scanner(System.in);
        System.out.println("请输入 10位整数：");
        int[] sort = new int[10];
        for(int i = 0; i < sort.length; i++){
            sort[i] = input.nextInt();
        }
        for (int i = 0; i < sort.length; i++){
            for(int j = 0; j < sort.length-i-1; j++){
                int temp;
                if (sort[j] > sort[j+1]){
                    temp = sort[j+1];
                    sort[j+1] = sort[j];
                    sort[j] = temp;
                }
            }
        }
        System.out.println("排序后的结果：");
        for(int i = 0; i < sort.length; i++){
            System.out.println(sort[i]);
        }
    }
}
