package com.xuyuchao.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.xuyuchao.gulimall.product.vo.spu.Attr;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author xuyuchao
 * @email 2672424338@qq.com
 * @date 2022-07-18 23:37:13
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);
    //保存sku的销售属性(pms_sku_sale_attr_value)
    void saveSkuSaleAttrs(Long skuId, List<Attr> attrs);
}

