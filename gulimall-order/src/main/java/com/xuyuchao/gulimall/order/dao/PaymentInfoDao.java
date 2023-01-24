package com.xuyuchao.gulimall.order.dao;

import com.xuyuchao.gulimall.order.entity.PaymentInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付信息表
 * 
 * @author xuyuchao
 * @email 2672424338@qq.com
 * @date 2022-07-19 10:47:07
 */
@Mapper
public interface PaymentInfoDao extends BaseMapper<PaymentInfoEntity> {
	
}
