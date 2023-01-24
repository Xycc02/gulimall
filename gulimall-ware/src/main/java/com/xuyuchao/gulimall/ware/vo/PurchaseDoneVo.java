package com.xuyuchao.gulimall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author: xuyuchao
 * @Date: 2022-08-12-23:24
 * @Description:
 */
@Data
public class PurchaseDoneVo {
    @NotNull
    private Long id;//采购单id
    private List<PurchaseItem> items;//采购需求集合
}
