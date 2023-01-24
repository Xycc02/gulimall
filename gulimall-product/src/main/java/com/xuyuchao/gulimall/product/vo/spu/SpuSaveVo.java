/**
  * Copyright 2022 bejson.com 
  */
package com.xuyuchao.gulimall.product.vo.spu;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Auto-generated: 2022-08-03 16:30:13
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 * spu完整信息
 */
@Data
public class SpuSaveVo {

    //表 pms_spu_info
    private String spuName;//商品名称
    private String spuDescription;//商品描述
    private Long catalogId;//所属分类id
    private Long brandId;//品牌id
    private BigDecimal weight;//商品重量
    private int publishStatus;//上架状态[0 - 下架，1 - 上架]

    //表 pms_spu_info_desc
    private List<String> descript;//商品介绍

    //表 pms_spu_images
    private List<String> images;//spu图片集

    //数据库 gulimall_sms  表 sms_spu_bounds
    private Bounds bounds;//spu积分信息

    //表 pms_product_attr_value
    private List<BaseAttrs> baseAttrs;//spu基本属性

    //(pms_sku_info  pms_sku_images  pms_sku_sale_attr_value)
    private List<Skus> skus;//sku信息

}