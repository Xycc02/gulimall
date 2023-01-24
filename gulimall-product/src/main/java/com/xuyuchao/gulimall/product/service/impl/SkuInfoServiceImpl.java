package com.xuyuchao.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuyuchao.common.to.SkuReductionTo;
import com.xuyuchao.common.utils.R;
import com.xuyuchao.gulimall.product.entity.SpuInfoEntity;
import com.xuyuchao.gulimall.product.feign.CouponFeignService;
import com.xuyuchao.gulimall.product.service.SkuImagesService;
import com.xuyuchao.gulimall.product.service.SkuSaleAttrValueService;
import com.xuyuchao.gulimall.product.vo.spu.Attr;
import com.xuyuchao.gulimall.product.vo.spu.Images;
import com.xuyuchao.gulimall.product.vo.spu.Skus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.common.utils.Query;

import com.xuyuchao.gulimall.product.dao.SkuInfoDao;
import com.xuyuchao.gulimall.product.entity.SkuInfoEntity;
import com.xuyuchao.gulimall.product.service.SkuInfoService;
import org.springframework.util.StringUtils;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private CouponFeignService couponFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 条件检索sku信息
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        //组装检索条件
        LambdaQueryWrapper<SkuInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)) {
            queryWrapper.and(spuInfoEntityLambdaQueryWrapper -> {
                spuInfoEntityLambdaQueryWrapper.eq(SkuInfoEntity::getSkuId,key)
                        .or()
                        .like(SkuInfoEntity::getSkuName,key)
                        .or()
                        .eq(SkuInfoEntity::getPrice,key);
            });
        }
        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId) && !"0".equals(brandId)) {
            queryWrapper.eq(SkuInfoEntity::getBrandId,brandId);
        }
        String catelogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId) && !"0".equals(catelogId)) {
            queryWrapper.eq(SkuInfoEntity::getCatalogId,catelogId);
        }
        String min = (String) params.get("min");
        String max = (String) params.get("max");
        if(!StringUtils.isEmpty(min) && !"0".equals(min)) {
            queryWrapper.ge(SkuInfoEntity::getPrice,min);
        }
        if(!StringUtils.isEmpty(max) && !"0".equals(max)) {
            queryWrapper.le(SkuInfoEntity::getPrice,max);
        }

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    /**
     * 根据SpuId查询所有Sku信息
     * @param spuId
     * @return
     */
    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        List<SkuInfoEntity> list = this.list(
                new LambdaQueryWrapper<SkuInfoEntity>()
                        .eq(SkuInfoEntity::getSpuId, spuId)
        );
        return list;
    }

    /**
     * 保存sku的基本信息
     * @param spuInfoEntity
     * @param skus
     */
    @Override
    public void saveSkuBaseInfo(SpuInfoEntity spuInfoEntity, List<Skus> skus) {
        if(!skus.isEmpty()) {
            skus.forEach(sku -> {
                /**
                 *     private String skuName;//sku名称
                 *     private BigDecimal price;//价格
                 *     private String skuTitle;//标题
                 *     private String skuSubtitle;//副标题
                 */
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, skuInfoEntity);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setSaleCount(0L);//sku销量
                //获取sku默认图片的url地址
                String defaultImg = "";
                for (Images image : sku.getImages()) {
                    if (image.getDefaultImg() == 1) {
                        defaultImg = image.getImgUrl();
                    }
                }
                skuInfoEntity.setSkuDefaultImg(defaultImg);//默认sku图片地址
                //6.1保存sku基本信息(下面sku关联信息需要skuId)
                this.save(skuInfoEntity);
                //6.2 保存sku的图片信息(pms_sku_images),因为sku图片信息中有sku_id
                skuImagesService.saveSkuImages(skuInfoEntity.getSkuId(),sku.getImages());
                //6.3 保存sku的销售属性(pms_sku_sale_attr_value)
                List<Attr> attrs = sku.getAttr();
                skuSaleAttrValueService.saveSkuSaleAttrs(skuInfoEntity.getSkuId(),attrs);

                //======远程调用========
                //6.4 保存sku的优惠满减信息(数据库gulimall_sms)
                //6.4.1 保存sku的满多少件打多少折信息(sms_sku_ladder)
                //6.4.2 保存sku的满多少钱减多少钱信息(sms_sku_full_reduction)
                //6.4.3 保存sku的会员价格表信息(sms_member_price)
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(sku,skuReductionTo);
                skuReductionTo.setSkuId(skuInfoEntity.getSkuId());
                //只有sku件数大于0或者满减金额数大于0或者会员价格集合不为空,长度不为0,才远程调用保存,并在被调用服务处再次判断是否添加数据库,少走rpc
                if(skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1 || !skuReductionTo.getMemberPrice().isEmpty()) {
                    R res = couponFeignService.saveSkuReduction(skuReductionTo);
                    if(res.getCode() != 0) {
                        log.error("远程调用保存优惠券信息失败!");
                    }
                }
            });
        }
    }
}