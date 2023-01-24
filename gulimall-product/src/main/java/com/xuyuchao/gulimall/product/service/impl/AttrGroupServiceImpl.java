package com.xuyuchao.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuyuchao.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.xuyuchao.gulimall.product.entity.AttrEntity;
import com.xuyuchao.gulimall.product.service.AttrAttrgroupRelationService;
import com.xuyuchao.gulimall.product.service.AttrService;
import com.xuyuchao.gulimall.product.vo.AttrGroupRelationVo;
import com.xuyuchao.gulimall.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.common.utils.Query;

import com.xuyuchao.gulimall.product.dao.AttrGroupDao;
import com.xuyuchao.gulimall.product.entity.AttrGroupEntity;
import com.xuyuchao.gulimall.product.service.AttrGroupService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {
    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;
    @Autowired
    private AttrService attrService;

    /**
     * 根据分类id获取属性分组
     * @param params
     * @param catelogId
     * @return
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {

        //根据分类id查询对应属性分组,若前端传来参数,进行模糊匹配
        //select * from pms_attr_group where catelog_id = catelogId and (attr_group_id = key or attr_group_name like %key% or descript like %key%)
        //封装参数条件
        String key = (String) params.get("key");
        LambdaQueryWrapper<AttrGroupEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(catelogId != 0,AttrGroupEntity::getCatelogId,catelogId)
                .orderByAsc(AttrGroupEntity::getSort);
        //前端传来关键字搜索
        if(!StringUtils.isEmpty(key)) {
            queryWrapper.and(attrGroupEntityLambdaQueryWrapper ->
                    attrGroupEntityLambdaQueryWrapper
                            .like(AttrGroupEntity::getAttrGroupName,key)
                            .or()
                            .like(AttrGroupEntity::getDescript,key));
        }

        IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),queryWrapper);
        return new PageUtils(page);
    }


    /**
     *删除属性和属性分组关联,前端传来[属性id,属性分组id]数组
     * @param relationVos
     */
    @Override
    @Transactional
    public void deleteRelation(AttrGroupRelationVo[] relationVos) {
        //遍历出数组的属性id和属性分组id
        if(relationVos.length > 0) {
            for (AttrGroupRelationVo relationVo : relationVos) {
                LambdaQueryWrapper<AttrAttrgroupRelationEntity> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(AttrAttrgroupRelationEntity::getAttrId,relationVo.getAttrId())
                        .eq(AttrAttrgroupRelationEntity::getAttrGroupId,relationVo.getAttrGroupId());
                attrAttrgroupRelationService.remove(queryWrapper);
            }
        }
    }

    /**
     * 根据分类id获取属性分组以及分组所对应属性
     * @param catelogId
     * @return
     */
    @Override
    public List<AttrGroupWithAttrsVo> getGroupWithAttrByCatelogId(Long catelogId) {
        //1.根据分类id获取该分类下的所有属性分组
        List<AttrGroupEntity> groupEntities = this.list(
                new QueryWrapper<AttrGroupEntity>()
                        .eq("catelog_id", catelogId)
        );
        if(groupEntities != null && groupEntities.size() > 0) {
            //2.封装分组信息,并获取该分组下的所有属性,封装到vo中
            List<AttrGroupWithAttrsVo> attrGroupWithAttrsVos = groupEntities.stream().map(group -> {
                AttrGroupWithAttrsVo attrGroupWithAttrsVo = new AttrGroupWithAttrsVo();
                //封装分组信息
                BeanUtils.copyProperties(group, attrGroupWithAttrsVo);
                //根据分组id获取对应属性
                List<AttrEntity> attrEntities = attrService.getRelationAttr(group.getAttrGroupId());
                if (attrEntities != null && attrEntities.size() > 0) {
                    attrGroupWithAttrsVo.setAttrs(attrEntities);
                }
                return attrGroupWithAttrsVo;
            }).collect(Collectors.toList());
            return attrGroupWithAttrsVos;
        }
        return null;
    }
}