package com.xuyuchao.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.gulimall.product.entity.ProductAttrValueEntity;
import com.xuyuchao.gulimall.product.vo.spu.BaseAttrs;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author xuyuchao
 * @email 2672424338@qq.com
 * @date 2022-07-18 23:37:13
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);
    //保存spu的基本属性(pms_product_attr_value)
    void saveBaseAttrs(Long id,List<BaseAttrs> baseAttrs);
    //根据spuId获取商品基本属性值信息
    List<ProductAttrValueEntity> getBaseAttrValueBySpuId(Long spuId);
}

