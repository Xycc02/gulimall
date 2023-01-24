package com.xuyuchao.gulimall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.common.utils.Query;

import com.xuyuchao.gulimall.product.dao.SpuImagesDao;
import com.xuyuchao.gulimall.product.entity.SpuImagesEntity;
import com.xuyuchao.gulimall.product.service.SpuImagesService;


@Service("spuImagesService")
public class SpuImagesServiceImpl extends ServiceImpl<SpuImagesDao, SpuImagesEntity> implements SpuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuImagesEntity> page = this.page(
                new Query<SpuImagesEntity>().getPage(params),
                new QueryWrapper<SpuImagesEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 保存spu图片集
     * @param id
     * @param images
     */
    @Override
    public void saveImages(Long id, List<String> images) {
        if(!images.isEmpty()) {
            List<SpuImagesEntity> imagesEntities = images.stream().map(img -> {
                SpuImagesEntity spuImages = new SpuImagesEntity();
                spuImages.setSpuId(id);
                spuImages.setImgUrl(img);
                return spuImages;
            }).collect(Collectors.toList());
            this.saveBatch(imagesEntities);
        }
    }
}