package com.xuyuchao.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xuyuchao.common.constant.WareConstant;
import com.xuyuchao.common.utils.R;
import com.xuyuchao.gulimall.ware.entity.PurchaseDetailEntity;
import com.xuyuchao.gulimall.ware.service.WareSkuService;
import com.xuyuchao.gulimall.ware.vo.PurchaseDoneVo;
import com.xuyuchao.gulimall.ware.vo.PurchaseItem;
import com.xuyuchao.gulimall.ware.vo.PurchaseMergeVo;
import com.xuyuchao.gulimall.ware.service.PurchaseDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.common.utils.Query;
import com.xuyuchao.gulimall.ware.dao.PurchaseDao;
import com.xuyuchao.gulimall.ware.entity.PurchaseEntity;
import com.xuyuchao.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService purchaseDetailService;
    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        LambdaQueryWrapper<PurchaseEntity> queryWrapper = new LambdaQueryWrapper<>();
        //封装检索条件
        String key = (String) params.get("key");
        String status = (String) params.get("status");
        queryWrapper.eq(!StringUtils.isEmpty(status),PurchaseEntity::getStatus,status)
                .orderByAsc(PurchaseEntity::getPriority);
        if(!StringUtils.isEmpty(key)) {
            queryWrapper.and(wrapper -> {
                wrapper.eq(PurchaseEntity::getId,key)
                        .or()
                        .like(PurchaseEntity::getAssigneeName,key)
                        .or()
                        .like(PurchaseEntity::getPhone,key)
                        .or()
                        .eq(PurchaseEntity::getWareId,key);
            });
        }
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    /**
     * 获取当前未分配或未领取的采购单
     * @return
     */
    @Override
    public List<PurchaseEntity> getUnreceivedPurchase() {
        List<PurchaseEntity> purchaseEntities = this.list(
                new LambdaQueryWrapper<PurchaseEntity>()
                        .eq(PurchaseEntity::getStatus, 0)
                        .or()
                        .eq(PurchaseEntity::getStatus, 1)
        );
        return purchaseEntities;
    }

    /**
     * 合并采购需求到采购单
     * @param purchaseMergeVo
     */
    @Override
    @Transactional
    public R merge(PurchaseMergeVo purchaseMergeVo) {

        //若采购需求的状态不是新建状态或者已分配状态,则不能合并到该采购单
        for (Long id : purchaseMergeVo.getItems()) {
            //根据采购需求id判断采购需求状态
            PurchaseDetailEntity purchaseDetailEntity = purchaseDetailService.getById(id);
            if(purchaseDetailEntity.getStatus() != WareConstant.PurchaseDetailEnum.CREATED.getCode() &&
                    purchaseDetailEntity.getStatus() != WareConstant.PurchaseDetailEnum.ASSIGNED.getCode()) {
                return R.error().put("msg","当前采购需求已被领取!");
            }
        }


        Long purchaseId = purchaseMergeVo.getPurchaseId();

        //情况一:将采购需求合并到一个新的采购单
        if(purchaseId == null) {
            //1.新建采购单
            PurchaseEntity purchase = new PurchaseEntity();
            purchase.setPriority(0);
            purchase.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            this.save(purchase);
            purchaseId = purchase.getId();
        }

        //合并采购需求到采购单
        for (Long id : purchaseMergeVo.getItems()) {
            //更新每个采购需求的采购单id和状态
            purchaseDetailService.update(
                    new LambdaUpdateWrapper<PurchaseDetailEntity>()
                            .set(PurchaseDetailEntity::getPurchaseId,purchaseId)
                            .set(PurchaseDetailEntity::getStatus,WareConstant.PurchaseDetailEnum.ASSIGNED.getCode())
                            .eq(PurchaseDetailEntity::getId,id)
            );
        }
        return R.ok();
    }

    /**
     * 采购人员领取采购单
     * @param purchaseIds
     */
    @Override
    public void received(List<Long> purchaseIds) {
        //1.确定当前采购单是新建或已分配状态
        List<PurchaseEntity> collect = purchaseIds.stream().map(id -> {
            PurchaseEntity purchaseEntity = this.getById(id);
            return purchaseEntity;
        }).filter(item -> {
            return (item.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode()) ||
                    (item.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode());
        }).map(item -> {
            item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVED.getCode());
            return item;
        }).collect(Collectors.toList());

        //2.改变采购单的状态
        this.updateBatchById(collect);

        //3.改变采购单对应采购需求的状态
        collect.forEach(item -> {
            //3.1.查询该采购单下的所有采购需求
            List<PurchaseDetailEntity> purchaseDetailEntities = purchaseDetailService.list(
                    new LambdaQueryWrapper<PurchaseDetailEntity>()
                            .eq(PurchaseDetailEntity::getPurchaseId, item.getId())
            );
            //3.2改变采购需求的状态
            purchaseDetailEntities.forEach(detail -> {
                detail.setStatus(WareConstant.PurchaseDetailEnum.BUYING.getCode());
            });
            //3.3批量修改
            purchaseDetailService.updateBatchById(purchaseDetailEntities);
        });
    }

    /**
     * 完成采购单
     * @param purchaseDoneVo
     */
    @Override
    @Transactional
    public void donePurchase(PurchaseDoneVo purchaseDoneVo) {

        //1.改变对应采购需求的状态
        Boolean flag = true;
        List<PurchaseDetailEntity> updates = new ArrayList<>();

        for (PurchaseItem item : purchaseDoneVo.getItems()) {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            if (item.getStatus() == WareConstant.PurchaseDetailEnum.ERROR.getCode()) {
                flag = false;
                purchaseDetailEntity.setStatus(item.getStatus());
            } else {
                purchaseDetailEntity.setStatus(item.getStatus());
                //将成功的采购进行入库
                PurchaseDetailEntity detailEntity = purchaseDetailService.getById(item.getItemId());
                wareSkuService.addStock(detailEntity.getSkuId(),detailEntity.getWareId(),detailEntity.getSkuNum());

            }
            purchaseDetailEntity.setId(item.getItemId());

            updates.add(purchaseDetailEntity);
        }
        purchaseDetailService.updateBatchById(updates);

        //2.改变采购单的状态
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseDoneVo.getId());
        purchaseEntity.setStatus(flag ? WareConstant.PurchaseStatusEnum.FINISHED.getCode() : WareConstant.PurchaseStatusEnum.ERROR.getCode());
        this.updateById(purchaseEntity);

    }
}