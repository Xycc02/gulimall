package com.xuyuchao.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.xuyuchao.gulimall.product.vo.AttrGroupRelationVo;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author xuyuchao
 * @email 2672424338@qq.com
 * @date 2022-07-18 23:37:13
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
    //添加属性和属性分组关系
    void saveRelation(List<AttrGroupRelationVo> relationVos);
}

