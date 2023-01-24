package com.xuyuchao.gulimall.order.dao;

import com.xuyuchao.gulimall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 * 
 * @author xuyuchao
 * @email 2672424338@qq.com
 * @date 2022-07-19 10:47:06
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {
	
}
