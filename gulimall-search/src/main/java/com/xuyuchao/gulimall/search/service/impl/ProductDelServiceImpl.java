package com.xuyuchao.gulimall.search.service.impl;

import com.xuyuchao.gulimall.search.service.ProductDelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: ProductDelServiceImpl
 * @Author XYC
 * @Date: 2023/3/14 11:19
 * @Version 1.0
 */

@Service
@Slf4j
public class ProductDelServiceImpl implements ProductDelService {


    /**
     * 删除skuId对应的的商品数据
     * @param skuIds
     * @return
     */
    @Override
    public boolean productDel(List<Long> skuIds) {
        log.info("异步删除ES中对应商品数据");
        //TODO 商品下架
        return true;
    }
}
