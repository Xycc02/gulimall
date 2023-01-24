package com.xuyuchao.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.common.utils.Query;
import com.xuyuchao.gulimall.product.dao.ProductAttrValueDao;
import com.xuyuchao.gulimall.product.entity.AttrEntity;
import com.xuyuchao.gulimall.product.entity.ProductAttrValueEntity;
import com.xuyuchao.gulimall.product.service.AttrService;
import com.xuyuchao.gulimall.product.service.ProductAttrValueService;
import com.xuyuchao.gulimall.product.vo.spu.BaseAttrs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Autowired
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 根据spuId获取对应的基本属性值信息
     * @param spuId
     * @return
     */
    @Override
    public List<ProductAttrValueEntity> getBaseAttrValueBySpuId(Long spuId) {
        List<ProductAttrValueEntity> attrValueEntities = this.list(
                new LambdaQueryWrapper<ProductAttrValueEntity>()
                        .eq(ProductAttrValueEntity::getSpuId, spuId)
        );
        return attrValueEntities;
    }

    /**
     * 保存spu的基本属性(pms_product_attr_value)
     * @param id
     * @param baseAttrs
     */
    @Override
    public void saveBaseAttrs(Long id,List<BaseAttrs> baseAttrs) {
        List<ProductAttrValueEntity> productAttrValueEntities = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setSpuId(id);
            productAttrValueEntity.setAttrId(attr.getAttrId());
            productAttrValueEntity.setAttrValue(attr.getAttrValues());
            productAttrValueEntity.setQuickShow(attr.getShowDesc());
            //根据基本属性id查询基本属性信息
            AttrEntity attrEntity = attrService.getById(attr.getAttrId());
            productAttrValueEntity.setAttrName(attrEntity.getAttrName());

            return productAttrValueEntity;
        }).collect(Collectors.toList());
        //批量保存spu的基本属性
        this.saveBatch(productAttrValueEntities);
    }
}