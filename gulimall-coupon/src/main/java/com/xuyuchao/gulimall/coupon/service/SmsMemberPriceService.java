package com.xuyuchao.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuyuchao.common.to.MemberPrice;
import com.xuyuchao.common.to.SkuReductionTo;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.gulimall.coupon.entity.SmsMemberPriceEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品会员价格
 *
 * @author xuyuchao
 * @email 2672424338@qq.com
 * @date 2022-08-04 17:19:28
 */
public interface SmsMemberPriceService extends IService<SmsMemberPriceEntity> {

    PageUtils queryPage(Map<String, Object> params);
    //6.4.3 保存sku的会员价格表信息(sms_member_price)
    void saveMemberPriceInfo(SkuReductionTo skuReductionTo);
}

