package com.xuyuchao.gulimall.coupon.service.impl;

import com.xuyuchao.common.to.SkuReductionTo;
import com.xuyuchao.gulimall.coupon.service.SkuLadderService;
import com.xuyuchao.gulimall.coupon.service.SmsMemberPriceService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.common.utils.Query;

import com.xuyuchao.gulimall.coupon.dao.SkuFullReductionDao;
import com.xuyuchao.gulimall.coupon.entity.SkuFullReductionEntity;
import com.xuyuchao.gulimall.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    private SkuLadderService skuLadderService;
    @Autowired
    private SmsMemberPriceService smsMemberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 保存sku满减信息
     * @param skuReductionTo
     */
    @Override
    public void saveSkuReduction(SkuReductionTo skuReductionTo) {
        //6.4 保存sku的优惠满减信息(数据库gulimall_sms)
        //6.4.1 保存sku的满多少件打多少折信息(sms_sku_ladder)
        if(skuReductionTo.getFullCount() > 0) {
            skuLadderService.saveLadderInfo(skuReductionTo);
        }
        //6.4.2 保存sku的满多少钱减多少钱信息(sms_sku_full_reduction)
        if(skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1) {
            this.saveFullReductionInfo(skuReductionTo);
        }
        //6.4.3 保存sku的会员价格表信息(sms_member_price)
        if(!skuReductionTo.getMemberPrice().isEmpty()) {
            smsMemberPriceService.saveMemberPriceInfo(skuReductionTo);
        }
    }

    /**
     * 6.4.2 保存sku的满多少钱减多少钱信息(sms_sku_full_reduction)
     * @param skuReductionTo
     */
    @Override
    public void saveFullReductionInfo(SkuReductionTo skuReductionTo) {
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTo,skuFullReductionEntity);
        this.save(skuFullReductionEntity);
    }
}