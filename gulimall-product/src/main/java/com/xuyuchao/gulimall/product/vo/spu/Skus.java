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
 * 商品的sku信息
 */
@Data
public class Skus {

    //pms_sku_sale_attr_value
    private List<Attr> attr;//sku的销售属性

    private String skuName;//sku名称
    private BigDecimal price;//价格
    private String skuTitle;//标题
    private String skuSubtitle;//副标题

    private List<Images> images;//sku图片集
    private List<String> descar;

    private int fullCount;//买了多少件数
    private BigDecimal discount;//打多少折
    private int countStatus;
    private BigDecimal fullPrice;//满多少钱
    private BigDecimal reducePrice;//减多少钱
    private int priceStatus;
    private List<MemberPrice> memberPrice;//会员价格信息

}