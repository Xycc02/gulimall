package com.xuyuchao.gulimall.coupon.service.impl;

import com.xuyuchao.common.to.MemberPrice;
import com.xuyuchao.common.to.SkuReductionTo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.common.utils.Query;

import com.xuyuchao.gulimall.coupon.dao.SmsMemberPriceDao;
import com.xuyuchao.gulimall.coupon.entity.SmsMemberPriceEntity;
import com.xuyuchao.gulimall.coupon.service.SmsMemberPriceService;


@Service("smsMemberPriceService")
public class SmsMemberPriceServiceImpl extends ServiceImpl<SmsMemberPriceDao, SmsMemberPriceEntity> implements SmsMemberPriceService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SmsMemberPriceEntity> page = this.page(
                new Query<SmsMemberPriceEntity>().getPage(params),
                new QueryWrapper<SmsMemberPriceEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 6.4.3 保存sku的会员价格表信息(sms_member_price)
     * @param skuReductionTo
     */
    @Override
    public void saveMemberPriceInfo(SkuReductionTo skuReductionTo) {
        Long skuId = skuReductionTo.getSkuId();
        List<MemberPrice> memberPrices = skuReductionTo.getMemberPrice();
        List<SmsMemberPriceEntity> smsMemberPriceEntities = memberPrices.stream().filter(item -> {
            //只过滤设置价格的会员
            return item.getPrice().compareTo(new BigDecimal("0"))  == 1;
        }).map(memberPrice -> {
            SmsMemberPriceEntity smsMemberPriceEntity = new SmsMemberPriceEntity();
            smsMemberPriceEntity.setSkuId(skuId);
            smsMemberPriceEntity.setMemberLevelId(memberPrice.getId());
            smsMemberPriceEntity.setMemberLevelName(memberPrice.getName());
            smsMemberPriceEntity.setMemberPrice(memberPrice.getPrice());
            smsMemberPriceEntity.setAddOther(1);
            return smsMemberPriceEntity;
        }).collect(Collectors.toList());
        //批量保存sku会员价格信息
        this.saveBatch(smsMemberPriceEntities);
    }
}