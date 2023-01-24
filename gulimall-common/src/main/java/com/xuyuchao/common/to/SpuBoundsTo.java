package com.xuyuchao.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: xuyuchao
 * @Date: 2022-08-04-22:31
 * @Description: 领域对象模型,用于不同模块之间json传输
 * 积分对象
 */
@Data
public class SpuBoundsTo {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
