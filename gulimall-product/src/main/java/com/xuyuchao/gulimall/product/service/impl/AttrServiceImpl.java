package com.xuyuchao.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuyuchao.common.constant.ProductConstant;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.common.utils.Query;
import com.xuyuchao.common.utils.R;
import com.xuyuchao.gulimall.product.dao.AttrDao;
import com.xuyuchao.gulimall.product.entity.*;
import com.xuyuchao.gulimall.product.service.*;
import com.xuyuchao.gulimall.product.vo.AttrRespVo;
import com.xuyuchao.gulimall.product.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private ProductAttrValueService productAttrValueService;

    /**
     * 根据spuId修改pu规格信息
     * @param spuId
     * @return
     */
    @Override
    public R updateBaseAttrById(Long spuId,List<ProductAttrValueEntity> baseAttrs) {
        baseAttrs.forEach(attr -> {
            attr.setSpuId(spuId);
            productAttrValueService.saveOrUpdate(attr,
                    new LambdaUpdateWrapper<ProductAttrValueEntity>()
                            .eq(ProductAttrValueEntity::getSpuId,spuId)
                            .eq(ProductAttrValueEntity::getAttrId,attr.getAttrId())
            );
        });
        return R.ok("保存spu规格信息成功!");
    }

    /**
     * 过滤出可被检索的(即search_type字段为1的属性)属性id
     * @param AttrIds
     * @return
     */
    @Override
    public List<Long> selectSearchAttrIds(List<Long> AttrIds) {
        return AttrIds.stream().filter(attrId -> {
            AttrEntity attrEntity = this.getById(attrId);
            return attrEntity.getSearchType() == 1;
        }).collect(Collectors.toList());
    }

    /**
     * 根据spuId获取spu规格信息
     * @param spuId
     * @return
     */
    @Override
    public R getBaseAttrById(Long spuId) {
        List<ProductAttrValueEntity> list = productAttrValueService.list(
                new QueryWrapper<ProductAttrValueEntity>()
                        .eq("spu_id", spuId)
        );
        return R.ok().put("data",list);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 保存前端传来的属性数据,并同步到其关联表中  (!!!注意:只有基本属性才有分组,销售属性是没有属性分组的)
     * @param attr
     */
    @Override
    @Transactional
    public void saveAttr(AttrVo attr) {
        //1.保存属性基本信息
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        this.save(attrEntity);
        //2.同步信息到关联表中(只有属性为基本类型时,才需要跟属性分组进行关联)
        if(attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
            attrAttrgroupRelationEntity.setAttrSort(0);
            attrAttrgroupRelationService.save(attrAttrgroupRelationEntity);
        }
    }

    /**
     * 根据分类id以及属性的分类(sale-销售属性-0，base-基本属性-1)获取对应属性信息
     * @param params
     * @param catelogId
     * @param attrType
     * @return
     */
    @Override
    public PageUtils queryAttrPage(Map<String, Object> params, Long catelogId, String attrType) {
        //封装参数条件
        String key = (String) params.get("key");
        LambdaQueryWrapper<AttrEntity> queryWrapper = new LambdaQueryWrapper<>();
        //当前端传来的catelogId值为0时,查询所有的属性,并过滤关键字key     再根据前端传来的属性分类过滤属性
        queryWrapper.eq(catelogId != 0,AttrEntity::getCatelogId,catelogId)
                //当前端路径参数attrType传来base,则为基本属性,对应数据库中1,若传来sale,则为销售属性,对应数据库中的0
                .eq(!StringUtils.isEmpty(attrType),AttrEntity::getAttrType,
                        ProductConstant.AttrEnum.ATTR_TYPE_BASE.getType().equalsIgnoreCase(attrType) ? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
        //当前端传来关键字搜索
        if(!StringUtils.isEmpty(key)) {
            queryWrapper.and(attrEntityLambdaQueryWrapper -> {
                attrEntityLambdaQueryWrapper.eq(AttrEntity::getAttrId,key)
                        .or()
                        .like(AttrEntity::getAttrName,key);
            });
        }

        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );

        PageUtils pageUtils = new PageUtils(page);

        //返回的AttrEntity没有对应分类名和分组名,需用VO类
        List<AttrEntity> records = page.getRecords();
        List<AttrRespVo> attrRespVoList = records.stream().map(attrEntity -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);
            //设置属性对应的分类名和属性分组名
            //1.根据分类id获取分类信息,并将分类名存入attrRespVo传给前端
            CategoryEntity categoryEntity = categoryService.getById(attrEntity.getCatelogId());
            if(categoryEntity != null)  {
                attrRespVo.setCatelogName(categoryEntity.getName());
            }
            // (!!!注意:只有基本属性才有分组,销售属性是没有属性分组的)
            if(ProductConstant.AttrEnum.ATTR_TYPE_BASE.getType().equalsIgnoreCase(attrType)) {
                //2.1利用中间表,根据属性id获取对应的属性分组id(一对多)
                AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationService.getOne(
                        new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                                .eq(AttrAttrgroupRelationEntity::getAttrId,attrEntity.getAttrId())
                );
                //2.2根据中间表获得属性分组id并查询得到属性分组信息,并将分组名存入attrRespVo传给前端
                if(relationEntity != null) {
                    if(relationEntity.getAttrGroupId() != null) {
                        AttrGroupEntity groupEntity = attrGroupService.getById(relationEntity.getAttrGroupId());
                        attrRespVo.setGroupName(groupEntity.getAttrGroupName());
                    }
                }
            }

            return attrRespVo;
        }).collect(Collectors.toList());

        pageUtils.setList(attrRespVoList);

        return pageUtils;
    }

    /**
     * 根据属性id获取属性vo信息(销售属性没有分组信息)
     * @param attrId
     * @return
     */
    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        //1.根据属性id获取基本的属性信息
        AttrEntity attrEntity = this.getById(attrId);
        //2.创建要返回的vo属性信息
        AttrRespVo attrRespVo = new AttrRespVo();
        BeanUtils.copyProperties(attrEntity,attrRespVo);
        //3.设置属性对应分类的分类id路径,以及设置属性对应属性分组的id
        //3.1设置属性对应分类的分类id路径,和分类名称
        Long[] cateLogPath = categoryService.findCateLogPath(attrEntity.getCatelogId());
        attrRespVo.setCatelogPath(cateLogPath);
        CategoryEntity categoryEntity = categoryService.getById(attrEntity.getCatelogId());
        if(categoryEntity != null)  {
            attrRespVo.setCatelogName(categoryEntity.getName());
        }
        //3.2设置属性对应属性分组的id,和分组名称 (销售属性没有属性分组)
        if(attrEntity.getAttrType().equals(ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode())) {
            AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationService.getOne(
                    new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                            .eq(AttrAttrgroupRelationEntity::getAttrId, attrId)
            );
            if(relationEntity != null) {
                //若关联表中属性的属性分组不为空,即绑定了属性,才设值
                if(relationEntity.getAttrGroupId() != null) {
                    AttrGroupEntity groupEntity = attrGroupService.getById(relationEntity.getAttrGroupId());
                    attrRespVo.setGroupName(groupEntity.getAttrGroupName());
                }
                Long attrGroupId = relationEntity.getAttrGroupId();
                attrRespVo.setAttrGroupId(attrGroupId);
            }

        }
        return attrRespVo;
    }

    /**
     * 修改属性信息
     * @param attr
     */
    @Override
    @Transactional
    public void updateAttr(AttrVo attr) {
        //前端传来AttrVo信息,将其中信息信息保存至数据库
        //1.修改属性表基本信息
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        this.updateById(attrEntity);
        //2.修改属性和属性分组关联表 (基本属性才有属性分组,才能修改)
        if(attr.getAttrType().equals(ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode())) {
            attrAttrgroupRelationService.update(
                    new UpdateWrapper<AttrAttrgroupRelationEntity>()
                            .set("attr_group_id" ,attr.getAttrGroupId())
                            .eq("attr_id",attr.getAttrId())
            );
        }
    }

    /**
     * 根据属性分组id获取对应属性
     * @param groupId
     * @return
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long groupId) {
        //根据属性分组id获取所有对应的属性id集合
        LambdaQueryWrapper<AttrAttrgroupRelationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AttrAttrgroupRelationEntity::getAttrGroupId,groupId);
        List<AttrAttrgroupRelationEntity> relationEntities = attrAttrgroupRelationService.list(queryWrapper);
        List<Long> attrIds = relationEntities.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
        if(attrIds.size() != 0) {
            //根据属性id集合获取对应属性集合
            List<AttrEntity> attrEntityList = (List<AttrEntity>) this.listByIds(attrIds);
            return attrEntityList;
        }
        return null;
    }

    /**
     * 查出分组id对应分类的属性,且该属性未关联过其他分组
     * @param params
     * @param groupId
     * @return
     */
    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long groupId) {
        //1.根据分组id查询对应分组信息
        AttrGroupEntity group = attrGroupService.getById(groupId);
        //2.根据分组所对应的分类id获取所有属性信息

        //3.过滤掉已经被关联过的属性
        //3.1 查询当前分类下分组(包括当前分组),并获取id
        List<AttrGroupEntity> groupEntities = attrGroupService.list(
                new LambdaQueryWrapper<AttrGroupEntity>()
                        .eq(AttrGroupEntity::getCatelogId, group.getCatelogId())
                        // .ne(AttrGroupEntity::getAttrGroupId, groupId)
        );
        List<Long> groupIds = groupEntities.stream().map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());

        if(groupIds.size() > 0) {
            //3.2查询这些分组的关联信息
            List<AttrAttrgroupRelationEntity> relationList = attrAttrgroupRelationService.list(
                    new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                            .in(AttrAttrgroupRelationEntity::getAttrGroupId, groupIds)
            );
            //3.3 拿到这些关联信息所对应的属性id集合
            List<Long> attrIds = relationList.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
            //3.4根据属性id集合获取所有属性,并取出这些关联了分组的属性(分页查询)(注意:只有基本属性才和分组关联)
            LambdaQueryWrapper<AttrEntity> wrapper = new LambdaQueryWrapper<AttrEntity>()
                    .eq(AttrEntity::getCatelogId, group.getCatelogId())
                    .notIn(attrIds.size() > 0,AttrEntity::getAttrId, attrIds)
                    .eq(AttrEntity::getAttrType,ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
            //关键字搜索
            String key = (String) params.get("key");
            if(!StringUtils.isEmpty(key)) {
                wrapper.and((w) -> {
                    w.eq(AttrEntity::getAttrId,key)
                            .or()
                            .like(AttrEntity::getAttrName,key);
                });
            }
            //分页查询
            IPage<AttrEntity> page = this.page(
                    new Query<AttrEntity>().getPage(params)
                    ,wrapper
            );

            return new PageUtils(page);
        }
        return null;
    }
}