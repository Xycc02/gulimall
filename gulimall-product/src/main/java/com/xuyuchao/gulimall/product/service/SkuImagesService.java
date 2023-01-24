package com.xuyuchao.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.gulimall.product.entity.SkuImagesEntity;
import com.xuyuchao.gulimall.product.vo.spu.Images;

import java.util.List;
import java.util.Map;

/**
 * sku图片
 *
 * @author xuyuchao
 * @email 2672424338@qq.com
 * @date 2022-07-18 23:37:13
 */
public interface SkuImagesService extends IService<SkuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);
    //保存sku的图片信息
    void saveSkuImages(Long skuId, List<Images> images);
}

