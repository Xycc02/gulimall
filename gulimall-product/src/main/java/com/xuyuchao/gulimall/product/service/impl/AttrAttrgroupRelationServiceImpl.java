package com.xuyuchao.gulimall.product.service.impl;

import com.xuyuchao.gulimall.product.vo.AttrGroupRelationVo;
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

import com.xuyuchao.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.xuyuchao.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.xuyuchao.gulimall.product.service.AttrAttrgroupRelationService;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrgroupRelationEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 添加属性和属性分组关系
     * @param relationVos
     */
    @Override
    public void saveRelation(List<AttrGroupRelationVo> relationVos) {
        //1.将每一个vo转成一个关系对象
        List<AttrAttrgroupRelationEntity> relationEntities = relationVos.stream().map(item -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item,relationEntity);
            relationEntity.setAttrSort(0);
            return relationEntity;
        }).collect(Collectors.toList());
        //2.批量保存
        this.saveBatch(relationEntities);
    }
}