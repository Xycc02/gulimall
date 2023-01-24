package com.xuyuchao.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuyuchao.common.to.SkuHasStockTo;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.gulimall.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author xuyuchao
 * @email 2672424338@qq.com
 * @date 2022-07-19 10:52:52
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);
    //添加商品库存
    void addStock(Long skuId, Long wareId, Integer skuNum);
    //根据skuId判断该商品是否有库存
    List<SkuHasStockTo> getSkusHasStock(List<Long> skuIds);


}

