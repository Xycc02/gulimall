package com.xuyuchao.gulimall.coupon.dao;

import com.xuyuchao.gulimall.coupon.entity.CouponHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券领取历史记录
 * 
 * @author xuyuchao
 * @email 2672424338@qq.com
 * @date 2022-07-19 10:23:52
 */
@Mapper
public interface CouponHistoryDao extends BaseMapper<CouponHistoryEntity> {
	
}
