package com.xuyuchao.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.gulimall.product.entity.SkuInfoEntity;
import com.xuyuchao.gulimall.product.entity.SpuInfoEntity;
import com.xuyuchao.gulimall.product.vo.spu.Skus;

import java.util.List;
import java.util.Map;

/**
 * sku信息
 *
 * @author xuyuchao
 * @email 2672424338@qq.com
 * @date 2022-07-18 23:37:13
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
    //保存sku的基本信息
    void saveSkuBaseInfo(SpuInfoEntity spuInfoEntity, List<Skus> skus);
    //条件检索sku信息
    PageUtils queryPageByCondition(Map<String, Object> params);
    //根据spuId查询出所有sku
    List<SkuInfoEntity> getSkusBySpuId(Long spuId);
}

