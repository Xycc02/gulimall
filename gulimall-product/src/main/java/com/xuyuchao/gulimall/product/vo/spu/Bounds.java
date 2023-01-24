/**
  * Copyright 2022 bejson.com 
  */
package com.xuyuchao.gulimall.product.vo.spu;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Auto-generated: 2022-08-03 16:30:13
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 * 购买商品获得积分(金币,成长值)
 */
@Data
public class Bounds {
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}