package com.xuyuchao.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: xuyuchao
 * @Date: 2022-08-04-23:05
 * @Description:
 */
@Data
public class SkuReductionTo {
    private Long skuId;//skuId
    private int fullCount;//买了多少件数
    private BigDecimal discount;//打多少折
    private int countStatus;//是否叠加其他优惠[0-不可叠加，1-可叠加]
    private BigDecimal fullPrice;//满多少钱
    private BigDecimal reducePrice;//减多少钱
    private int priceStatus;
    private List<MemberPrice> memberPrice;//会员价格信息
}
