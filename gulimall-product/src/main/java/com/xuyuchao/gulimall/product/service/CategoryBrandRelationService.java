package com.xuyuchao.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.gulimall.product.entity.BrandEntity;
import com.xuyuchao.gulimall.product.entity.CategoryBrandRelationEntity;
import com.xuyuchao.gulimall.product.vo.BrandVo;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author xuyuchao
 * @email 2672424338@qq.com
 * @date 2022-07-18 23:37:13
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
    //保存品牌和分类的关系,前端只传过来品牌id和分类id,我们将根据这两个数据从表中获得分类名和品牌名存入该表,避免大表关联查询,影响数据库性能
    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);
    //根据品牌id修改品牌名
    void updateBrand(Long brandId, String name);
    //根据分类id修改分类名
    void updateCategory(Long catId, String name);
    //根据商品分类id获取所有对应品牌信息
    List<BrandEntity> getBrandsByCatId(Long catId);
}

