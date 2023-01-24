package com.xuyuchao.common.to;

import lombok.Data;

/**
 * @Author: xuyuchao
 * @Date: 2022-11-16-20:46
 * @Description:
 */
@Data
public class SkuHasStockTo {
    private Long skuId;
    private boolean hasStock;
}
