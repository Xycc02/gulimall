package com.xuyuchao.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.gulimall.product.entity.SpuCommentEntity;

import java.util.Map;

/**
 * 商品评价
 *
 * @author xuyuchao
 * @email 2672424338@qq.com
 * @date 2022-07-18 23:37:13
 */
public interface SpuCommentService extends IService<SpuCommentEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

