package com.atguigu.gmall.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.manager.BaseAttrInfo;
import com.atguigu.gmall.manager.BaseAttrInfoService;
import com.atguigu.gmall.manager.BaseAttrValue;
import com.atguigu.gmall.manager.mapper.BaseAttrInfoMapper;
import com.atguigu.gmall.manager.mapper.BaseAttrValueMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class BaseAttrInfoServiceImpl implements BaseAttrInfoService {
    @Autowired
    BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    BaseAttrValueMapper baseAttrValueMapper;

    @Override
    public List<BaseAttrInfo> getBaseAttrInfoByCatalog3Id(Integer Catalog3Id) {
        return  baseAttrInfoMapper.selectList(new QueryWrapper<BaseAttrInfo>()
                .eq("catalog3_id", Catalog3Id));

    }

    @Override
    public List<BaseAttrValue> getBaseAttrValueByAttrId(Integer AttrId) {
        return baseAttrValueMapper.selectList(new QueryWrapper<BaseAttrValue>()
                .eq("attr_id",AttrId));
    }

    /**
     * 大型的增删改方法实现
     *
     * springBoot 开启事务
     * (1)在实现方法上加上 @Transactional
     * (2)在提供者的main()上添加 @EnableTransactionManagement
     * @param baseAttrInfo
     */
    @Transactional
    @Override
    public void saveOrUpdateBaseInfo(BaseAttrInfo baseAttrInfo) {
        log.info("saveOrUpdateBaseInfo()收到的消息是：{}",baseAttrInfo);
        //判断是修改，保存，还是删除
        if(baseAttrInfo.getId() != null){
            log.info("走到baseAttrInfo.getId() != null。。。。。。。。。。。。。。。。。。。。。。。");
            //1、修改基本属性名
            baseAttrInfoMapper.updateById(baseAttrInfo);
            //2、属性的属性值操作
            List<BaseAttrValue> attrValues = baseAttrInfo.getAttrValues();
            ArrayList<Integer> ids = new ArrayList<>();
            for (BaseAttrValue attrValue : attrValues) {
                Integer id = attrValue.getId();
                if(id != null){
                    ids.add(id);
                }

            }

            //2.1 删除没有提交过来的数据
            baseAttrValueMapper.delete(new QueryWrapper<BaseAttrValue>()
                    .notIn("id",ids).eq("attr_id",baseAttrInfo.getId()));

            //一个循环实现修改和添加
            for (BaseAttrValue attrValue : attrValues) {
                //2.2 提交过来的数据有id就是修改
                if(attrValue.getId() != null){
                    baseAttrValueMapper.updateById(attrValue);
                }else{
                    //2.3 新增
                    log.info("看看新增是否有attrId：{}",attrValue);
                    baseAttrValueMapper.insert(attrValue);
                }
            }

        }
        else{
            log.info("是否走到这。。。。。。。。。。。。。");
            //AttrInfo 的添加功能
            baseAttrInfoMapper.insert(baseAttrInfo);

        }
    }
}
