package com.xuyuchao.gulimall.ware.vo;

import lombok.Data;

/**
 * @Author: xuyuchao
 * @Date: 2022-08-08-21:52
 * @Description: 订单需求合并到采购单vo
 */
@Data
public class PurchaseMergeVo {
    private Long[] items;//合并的采购需求id
    private Long purchaseId;//合并到的采购单id,若前端传来的该属性为空,则手动创建一个新的采购单,状态为新建
}
