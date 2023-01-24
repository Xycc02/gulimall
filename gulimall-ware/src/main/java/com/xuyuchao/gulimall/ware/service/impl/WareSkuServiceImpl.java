package com.xuyuchao.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuyuchao.common.to.SkuHasStockTo;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.common.utils.Query;
import com.xuyuchao.common.utils.R;
import com.xuyuchao.gulimall.ware.dao.WareSkuDao;
import com.xuyuchao.gulimall.ware.entity.WareSkuEntity;
import com.xuyuchao.gulimall.ware.feign.ProductFeignService;
import com.xuyuchao.gulimall.ware.service.WareSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private WareSkuDao wareSkuDao;
    @Autowired
    private ProductFeignService productFeignService;

    /**
     * 添加商品库存
     * @param skuId
     * @param wareId
     * @param skuNum
     */
    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //若当前商品不存在则新增操作,新增商品以及库存
        Integer count = wareSkuDao.selectCount(
                new LambdaQueryWrapper<WareSkuEntity>()
                        .eq(WareSkuEntity::getSkuId, skuId)
                        .eq(WareSkuEntity::getWareId, wareId)
        );
        if(count > 0) {
            wareSkuDao.addStock(skuId, wareId, skuNum);
        }else {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            //获取对应skuId的商品名称
            try {
                R res = productFeignService.info(skuId);
                Map<String,Object> skuInfo = (Map<String, Object>) res.get("skuInfo");

                if(res.getCode() == 0) {
                    wareSkuEntity.setSkuName((String) skuInfo.get("skuName"));
                }
            }catch (Exception e) {

            }

            wareSkuDao.insert(wareSkuEntity);
        }

    }

    /**
     * 根据skuId判断该商品是否有库存
     * @return
     */
    @Override
    public List<SkuHasStockTo> getSkusHasStock(List<Long> skuIds) {
        return skuIds.stream().map(skuId -> {
            SkuHasStockTo skuHasStockTo = new SkuHasStockTo();
            //查询skuId对应的库存量  SELECT SUM(stock-stock_locked) FROM `wms_ware_sku` where sku_id = 43
            Long count = wareSkuDao.getSkuStock(skuId);
            skuHasStockTo.setSkuId(skuId);
            skuHasStockTo.setHasStock(count == null ? false:count>0);
            return skuHasStockTo;
        }).collect(Collectors.toList());
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        /**
         * skuId: 3
         * wareId: 1
         */
        LambdaQueryWrapper<WareSkuEntity> queryWrapper = new LambdaQueryWrapper<>();
        String skuId = (String) params.get("skuId");
        String wareId = (String) params.get("wareId");

        queryWrapper.eq(!StringUtils.isEmpty(wareId),WareSkuEntity::getWareId,wareId)
                .eq(!StringUtils.isEmpty(skuId),WareSkuEntity::getSkuId,skuId);
        //封装检索条件
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

}