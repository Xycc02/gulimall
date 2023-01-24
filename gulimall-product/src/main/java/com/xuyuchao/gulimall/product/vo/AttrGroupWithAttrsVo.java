package com.xuyuchao.gulimall.product.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.xuyuchao.gulimall.product.entity.AttrEntity;
import lombok.Data;

import java.util.List;

/**
 * @Author: xuyuchao
 * @Date: 2022-08-02-14:53
 * @Description:
 */
@Data
public class AttrGroupWithAttrsVo {
    /**
     * 分组id
     */
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;
    /**
     * 属性分组对应的所有基本属性
     */
    private List<AttrEntity> attrs;
}
