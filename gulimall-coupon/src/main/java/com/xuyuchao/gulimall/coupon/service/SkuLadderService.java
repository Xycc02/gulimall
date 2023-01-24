package com.xuyuchao.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuyuchao.common.to.SkuReductionTo;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.gulimall.coupon.entity.SkuLadderEntity;

import java.util.Map;

/**
 * 商品阶梯价格
 *
 * @author xuyuchao
 * @email 2672424338@qq.com
 * @date 2022-07-19 10:23:52
 */
public interface SkuLadderService extends IService<SkuLadderEntity> {

    PageUtils queryPage(Map<String, Object> params);
    //6.4.1 保存sku的满多少件打多少折信息(sms_sku_ladder)
    void saveLadderInfo(SkuReductionTo skuReductionTo);
}

