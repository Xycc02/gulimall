package com.xuyuchao.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuyuchao.common.to.SkuReductionTo;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.gulimall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author xuyuchao
 * @email 2672424338@qq.com
 * @date 2022-07-19 10:23:52
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);
    //保存sku满减信息
    void saveSkuReduction(SkuReductionTo skuReductionTo);
    //6.4.2 保存sku的满多少钱减多少钱信息(sms_sku_full_reduction)
    void saveFullReductionInfo(SkuReductionTo skuReductionTo);
}

