package com.xuyuchao.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.gulimall.product.entity.CategoryEntity;
import com.xuyuchao.gulimall.product.vo.Catelog2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author xuyuchao
 * @email 2672424338@qq.com
 * @date 2022-07-18 23:37:13
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
    //查出所有分类以及子分类,以树形结构组装
    List<CategoryEntity> listWithTree();
    //批量删除分类
    void removeMenuByIds(List<Long> asList);
    //根据分类id获取分类id的全路径id
    Long[] findCateLogPath(Long catelogId);
    //修改分类信息,并将品牌分类表中的冗余字段更改
    void updateDetail(CategoryEntity category);
    //查出所有的一级分类
    List<CategoryEntity> getLevel1Categories();
    //查询并封装二级分类Catelog2Vo集合
    Map<String, List<Catelog2Vo>> getCatelog2Vos();

}

