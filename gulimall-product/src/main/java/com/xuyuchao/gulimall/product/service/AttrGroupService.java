package com.xuyuchao.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.gulimall.product.entity.AttrEntity;
import com.xuyuchao.gulimall.product.entity.AttrGroupEntity;
import com.xuyuchao.gulimall.product.vo.AttrGroupRelationVo;
import com.xuyuchao.gulimall.product.vo.AttrGroupWithAttrsVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author xuyuchao
 * @email 2672424338@qq.com
 * @date 2022-07-18 23:37:13
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    //根据分类id获取属性分组
    PageUtils queryPage(Map<String, Object> params, Long catelogId);
    //删除属性和属性分组关联,前端传来[属性id,属性分组id]数组
    void deleteRelation(AttrGroupRelationVo[] relationVos);
    //根据分类id获取属性分组以及分组所对应属性
    List<AttrGroupWithAttrsVo> getGroupWithAttrByCatelogId(Long catelogId);
}

