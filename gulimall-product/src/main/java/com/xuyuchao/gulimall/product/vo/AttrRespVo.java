package com.xuyuchao.gulimall.product.vo;

import lombok.Data;

/**
 * @Author: xuyuchao
 * @Date: 2022-07-29-13:51
 * @Description:
 */
@Data
public class AttrRespVo extends AttrVo{
    /**
     * 所属分类名
     */
    private String catelogName;
    /**
     * 所属分组名
     */
    private String groupName;
    /**
     * 分类id路径
     */
    private Long[] catelogPath;

}
