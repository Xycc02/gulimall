package com.xuyuchao.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.common.utils.R;
import com.xuyuchao.gulimall.product.entity.AttrEntity;
import com.xuyuchao.gulimall.product.entity.ProductAttrValueEntity;
import com.xuyuchao.gulimall.product.vo.AttrRespVo;
import com.xuyuchao.gulimall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author xuyuchao
 * @email 2672424338@qq.com
 * @date 2022-07-18 23:37:13
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);
    //保存前端传来的属性数据,并同步到其关联表中
    void saveAttr(AttrVo attr);
    //根据分类id以及属性的分类(sale-销售属性-0，base-基本属性-1)获取对应属性信息
    PageUtils queryAttrPage(Map<String, Object> params, Long catelogId, String attrType);
    //根据属性id获取属性vo信息
    AttrRespVo getAttrInfo(Long attrId);
    //修改属性信息
    void updateAttr(AttrVo attr);
    //根据属性分组id获取对应属性
    List<AttrEntity> getRelationAttr(Long groupId);
    //查出分组id对应分类的属性,且该属性未关联过其他分组
    PageUtils getNoRelationAttr(Map<String, Object> params, Long groupId);
    //根据spuId获取spu规格信息
    R getBaseAttrById(Long spuId);
    //根据spuId修改pu规格信息
    R updateBaseAttrById(Long spuId,List<ProductAttrValueEntity> baseAttrs);
    //过滤出可被检索的(即search_type字段为1的属性)属性id
    List<Long> selectSearchAttrIds(List<Long> AttrIds);


}

