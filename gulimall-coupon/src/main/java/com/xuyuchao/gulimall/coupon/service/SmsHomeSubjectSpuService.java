package com.xuyuchao.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.gulimall.coupon.entity.SmsHomeSubjectSpuEntity;

import java.util.Map;

/**
 * 专题商品
 *
 * @author xuyuchao
 * @email 2672424338@qq.com
 * @date 2022-08-04 17:19:28
 */
public interface SmsHomeSubjectSpuService extends IService<SmsHomeSubjectSpuEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

