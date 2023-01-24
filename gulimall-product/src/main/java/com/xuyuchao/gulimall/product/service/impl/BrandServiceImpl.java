package com.xuyuchao.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.common.utils.Query;
import com.xuyuchao.gulimall.product.dao.BrandDao;
import com.xuyuchao.gulimall.product.entity.BrandEntity;
import com.xuyuchao.gulimall.product.service.BrandService;
import com.xuyuchao.gulimall.product.service.CategoryBrandRelationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = (String)params.get("key");
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                new QueryWrapper<BrandEntity>()
                        .like(StringUtils.isNotBlank(key),"name", key)
                        .orderByAsc("sort")
        );

        return new PageUtils(page);
    }

    /**
     * 修改品牌信息以及关联的品牌分类表中的冗余字段
     * @param brand
     */
    @Override
    @Transactional
    public void updateDetail(BrandEntity brand) {
        //1.根据品牌id修改品牌信息
        this.updateById(brand);
        //2.同步其他关联表中的数据
        if(StringUtils.isNotEmpty(brand.getName())) {
            categoryBrandRelationService.updateBrand(brand.getBrandId(),brand.getName());
            //TODO 同步其他冗余字段
        }
    }
}