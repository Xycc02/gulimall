package com.xuyuchao.gulimall.ware.vo;

import lombok.Data;

/**
 * @Author: xuyuchao
 * @Date: 2022-08-12-23:25
 * @Description:
 */
@Data
public class PurchaseItem {
    private Long itemId;    //采购需求id
    private Integer status; //采购需求状态
    private String reason;  //采购需求失败原因
}
