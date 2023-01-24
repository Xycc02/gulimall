package com.xuyuchao.gulimall.product.service.impl;

import com.xuyuchao.gulimall.product.vo.spu.Images;
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

import com.xuyuchao.gulimall.product.dao.SkuImagesDao;
import com.xuyuchao.gulimall.product.entity.SkuImagesEntity;
import com.xuyuchao.gulimall.product.service.SkuImagesService;
import org.springframework.util.StringUtils;


@Service("skuImagesService")
public class SkuImagesServiceImpl extends ServiceImpl<SkuImagesDao, SkuImagesEntity> implements SkuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuImagesEntity> page = this.page(
                new Query<SkuImagesEntity>().getPage(params),
                new QueryWrapper<SkuImagesEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 保存sku的图片信息
     * @param skuId
     * @param images
     */
    @Override
    public void saveSkuImages(Long skuId, List<Images> images) {
        //保存skuId的图片信息
        List<SkuImagesEntity> skuImagesEntities = images.stream().filter(item -> {
            //过滤出url不为空的图片,因为前端sku选择图片时,是把勾选的图片附带url带来,没有勾选的是直接把url置为空发来
            return !StringUtils.isEmpty(item.getImgUrl()) ;
        }).map(img -> {
            SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
            BeanUtils.copyProperties(img, skuImagesEntity);
            skuImagesEntity.setSkuId(skuId);
            skuImagesEntity.setImgSort(0);
            return skuImagesEntity;
        }).collect(Collectors.toList());
        //批量保存sku图片信息
        this.saveBatch(skuImagesEntities);
    }
}