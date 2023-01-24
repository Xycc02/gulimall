package com.xuyuchao.gulimall.product.service.impl;

import com.xuyuchao.gulimall.product.vo.spu.Attr;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.common.utils.Query;

import com.xuyuchao.gulimall.product.dao.SkuSaleAttrValueDao;
import com.xuyuchao.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.xuyuchao.gulimall.product.service.SkuSaleAttrValueService;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 保存sku的销售属性(pms_sku_sale_attr_value)
     * @param skuId
     * @param attrs
     */
    @Override
    public void saveSkuSaleAttrs(Long skuId, List<Attr> attrs) {
        List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attrs.stream().map(attr -> {
            SkuSaleAttrValueEntity saleAttrValueEntity = new SkuSaleAttrValueEntity();
            BeanUtils.copyProperties(attr, saleAttrValueEntity);
            saleAttrValueEntity.setSkuId(skuId);
            return saleAttrValueEntity;
        }).collect(Collectors.toList());
        //批量保存sku销售属性值
        this.saveBatch(skuSaleAttrValueEntities);
    }
}