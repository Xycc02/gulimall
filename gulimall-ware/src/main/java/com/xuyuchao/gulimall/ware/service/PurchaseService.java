package com.xuyuchao.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.common.utils.R;
import com.xuyuchao.gulimall.ware.entity.PurchaseEntity;
import com.xuyuchao.gulimall.ware.vo.PurchaseDoneVo;
import com.xuyuchao.gulimall.ware.vo.PurchaseMergeVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author xuyuchao
 * @email 2672424338@qq.com
 * @date 2022-07-19 10:52:52
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);
    //获取当前未分配或未领取的采购单
    List<PurchaseEntity> getUnreceivedPurchase();
    //合并采购需求到采购单
    R merge(PurchaseMergeVo purchaseMergeVo);
    //采购人员领取采购单
    void received(List<Long> purchaseIds);
    //完成采购单
    void donePurchase(PurchaseDoneVo purchaseDoneVo);
}

