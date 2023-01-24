package com.xuyuchao.gulimall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.xuyuchao.gulimall.ware.vo.PurchaseDoneVo;
import com.xuyuchao.gulimall.ware.vo.PurchaseMergeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.xuyuchao.gulimall.ware.entity.PurchaseEntity;
import com.xuyuchao.gulimall.ware.service.PurchaseService;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.common.utils.R;



/**
 * 采购信息
 *
 * @author xuyuchao
 * @email 2672424338@qq.com
 * @date 2022-07-19 10:52:52
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    /**
     * 列表
     */
    @RequestMapping("/list")
        public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 完成采购单
     * @param purchaseDoneVo
     * @return
     */
    @PostMapping("/done")
    public R donePurchase(@RequestBody PurchaseDoneVo purchaseDoneVo) {
        purchaseService.donePurchase(purchaseDoneVo);
        return R.ok();
    }

    /**
     * 采购人员领取采购单
     * @param purchaseIds
     * @return
     */
    @PostMapping("/received")
    public R received(@RequestBody List<Long> purchaseIds) {
        purchaseService.received(purchaseIds);
        return R.ok();
    }

    /**
     * 获取当前未分配或未领取的采购单
     * @return
     */
    @GetMapping("/unreceive/list")
    public R getUnreceivedPurchase() {
        List<PurchaseEntity> data = purchaseService.getUnreceivedPurchase();
        return R.ok().put("data",data);
    }

    /**
     * 合并采购需求到采购单
     * @param purchaseMergeVo
     * @return
     */
    @PostMapping("/merge")
    public R merge(@RequestBody PurchaseMergeVo purchaseMergeVo) {
        R res = purchaseService.merge(purchaseMergeVo);
        return res;
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
        public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
        public R save(@RequestBody PurchaseEntity purchase){
		purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
        public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
        public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
