package com.xuyuchao.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xuyuchao.common.constant.ProductConstant;
import com.xuyuchao.common.to.SkuHasStockTo;
import com.xuyuchao.common.to.SpuBoundsTo;
import com.xuyuchao.common.to.es.SkuEsModel;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.common.utils.Query;
import com.xuyuchao.common.utils.R;
import com.xuyuchao.gulimall.product.dao.SpuInfoDao;
import com.xuyuchao.gulimall.product.entity.*;
import com.xuyuchao.gulimall.product.feign.CouponFeignService;
import com.xuyuchao.gulimall.product.feign.SearchFeignService;
import com.xuyuchao.gulimall.product.feign.WareFeignService;
import com.xuyuchao.gulimall.product.service.*;
import com.xuyuchao.gulimall.product.vo.spu.BaseAttrs;
import com.xuyuchao.gulimall.product.vo.spu.Skus;
import com.xuyuchao.gulimall.product.vo.spu.SpuSaveVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private SpuImagesService spuImagesService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private CouponFeignService couponFeignService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private AttrService attrService;
    @Autowired
    private WareFeignService wareFeignService;
    @Autowired
    private SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 1.保存spu的基本信息(pms_spu_info)
     * @param spuInfoEntity
     */
    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

    /**
     * 保存商品信息
     * @param spuSaveVo
     */
    @Override
    @Transactional
    public void saveSpuInfo(SpuSaveVo spuSaveVo) {
        //1.保存spu的基本信息(pms_spu_info)
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo,spuInfoEntity);
        this.saveBaseSpuInfo(spuInfoEntity);
        //2.保存spu的描述图片(pms_spu_info_desc)
        List<String> descripts = spuSaveVo.getDescript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",",descripts));
        spuInfoDescService.save(spuInfoDescEntity);
        //3.保存spu的图片集(pms_spu_images)
        List<String> images = spuSaveVo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(),images);
        //4.保存spu的基本属性(pms_product_attr_value)
        List<BaseAttrs> baseAttrs = spuSaveVo.getBaseAttrs();
        productAttrValueService.saveBaseAttrs(spuInfoEntity.getId(),baseAttrs);
        //5.保存spu的积分信息(数据库 gulimall_sms  表 sms_spu_bounds) 远程调用
        SpuBoundsTo spuBoundsTo = new SpuBoundsTo();
        BeanUtils.copyProperties(spuSaveVo.getBounds(),spuBoundsTo);
        spuBoundsTo.setSpuId(spuInfoEntity.getId());
        R res = couponFeignService.saveSpuBounds(spuBoundsTo);
        if(res.getCode() != 0) {
            log.error("远程调用保存积分信息失败!");
        }

        //6.保存当前spu的sku信息
        //6.1 保存sku的基本信息(pms_sku_info)
        //6.2 保存sku的图片信息(pms_sku_images)
        //6.3 保存sku的销售属性(pms_sku_sale_attr_value)
        //6.4 保存sku的优惠满减信息(数据库gulimall_sms)
        //6.4.1 保存sku的满多少件打多少折信息(sms_sku_ladder)
        //6.4.2 保存sku的满多少钱减多少钱信息(sms_sku_full_reduction)
        //6.4.3 保存sku的会员价格表信息(sms_member_price)
        List<Skus> skus = spuSaveVo.getSkus();
        skuInfoService.saveSkuBaseInfo(spuInfoEntity,skus);

    }

    /**
     * 根据条件检索spu信息
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        //组装检索条件
        LambdaQueryWrapper<SpuInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)) {
            queryWrapper.and(spuInfoEntityLambdaQueryWrapper -> {
                spuInfoEntityLambdaQueryWrapper.eq(SpuInfoEntity::getId,key)
                        .or()
                        .like(SpuInfoEntity::getSpuName,key)
                        .or()
                        .like(SpuInfoEntity::getSpuDescription,key);
            });
        }
        String status = (String) params.get("status");
        if(!StringUtils.isEmpty(status)) {
            queryWrapper.eq(SpuInfoEntity::getPublishStatus,status);
        }
        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId) && !"0".equals(brandId)) {
            queryWrapper.eq(SpuInfoEntity::getBrandId,brandId);
        }
        String catelogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId) && !"0".equals(catelogId)) {
            queryWrapper.eq(SpuInfoEntity::getCatalogId,catelogId);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                queryWrapper
        );

        //将品牌名和分类名加入该对象中返回前端
        List<SpuInfoEntity> records = page.getRecords().stream().map(spu -> {
            CategoryEntity category = categoryService.getById(spu.getCatalogId());
            BrandEntity brand = brandService.getById(spu.getBrandId());
            spu.setCatalogName(category.getName());
            spu.setBrandName(brand.getName());
            return spu;
        }).collect(Collectors.toList());

        page.setRecords(records);

        return new PageUtils(page);
    }

    /**
     * 商品上架
     * @param spuId
     */
    @Override
    public void up(Long spuId) {
        //1.根据spuId查询所有sku信息
        List<SkuInfoEntity> skuList = skuInfoService.getSkusBySpuId(spuId);
        //统一查询该spu对应的 brandName brandImg catalogName
        BrandEntity brand = brandService.getById(skuList.get(0).getBrandId());
        CategoryEntity category = categoryService.getById(skuList.get(0).getCatalogId());
        //统一查询该sku对应的基本属性(可检索)
        List<ProductAttrValueEntity> baseAttrs = productAttrValueService.getBaseAttrValueBySpuId(spuId);
        List<Long> AttrIds = baseAttrs.stream().map(baseAttr -> {
            return baseAttr.getAttrId();
        }).collect(Collectors.toList());
        //过滤出可被检索的(即search_type字段为1的属性)属性id
        List<Long> searchAttrIds = attrService.selectSearchAttrIds(AttrIds);
        //将可被检索的属性id转化为Set集合,方便判断属性id是否存在
        Set<Long> searchAttrIdSet = new HashSet<>(searchAttrIds);
        //根据可被检索的属性id的set集合过滤出属性集合
        List<SkuEsModel.Attr> attrs = new ArrayList<>();
        List<SkuEsModel.Attr> skuEsModelAttrs = baseAttrs.stream().filter(baseAttr -> {
            return searchAttrIdSet.contains(baseAttr.getAttrId());
        }).map(item -> {
            SkuEsModel.Attr attr = new SkuEsModel.Attr();
            BeanUtils.copyProperties(item,attr);
            attrs.add(attr);
            return attr;
        }).collect(Collectors.toList());

        //TODO 远程调用库存服务获取skuId集合是否有库存
        List<Long> skuIdList = skuList.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
        //远程调用
        Map<Long, Boolean> skuHasStockMap = null;
        try {
            R skusHasStock = wareFeignService.getSkusHasStock(skuIdList);
            //skuHasStockMap = skusHasStock.getData().stream().collect(Collectors.toMap(SkuHasStockTo::getSkuId, SkuHasStockTo::isHasStock));
            List<SkuHasStockTo> skuHasStockToList = (List<SkuHasStockTo>) skusHasStock.get("data");
            /**
             * 此处解决了Feign远程调用时,对象转为LinkedHashMap的bug
             */
            ObjectMapper objectMapper = new ObjectMapper();
            skuHasStockToList = objectMapper.convertValue(skuHasStockToList, new TypeReference<List<SkuHasStockTo>>() {});
            skuHasStockMap = skuHasStockToList.stream().collect(Collectors.toMap(SkuHasStockTo::getSkuId, SkuHasStockTo::isHasStock));
        } catch (Exception e) {
            log.error("查询库存服务出现异常,原因{}",e);
        }

        //2.将sku信息拷入SkuEsModel对象中用于商品上架
        Map<Long, Boolean> finalSkuHasStockMap = skuHasStockMap;
        List<SkuEsModel> skuEsModels = skuList.stream().map(sku -> {
            SkuEsModel skuEsModel = new SkuEsModel();
            //单独设置额外属性
            skuEsModel.setSkuTitle(sku.getSkuName());
            skuEsModel.setSkuPrice(sku.getPrice());
            skuEsModel.setSkuImg(sku.getSkuDefaultImg());
            skuEsModel.setHotScore(0L);//商品热度评分默认0 TODO 扩展
            skuEsModel.setBrandName(brand.getName());
            skuEsModel.setBrandImg(brand.getLogo());
            skuEsModel.setCatalogName(category.getName());
            //设置当前skuId是否有库存
            if(finalSkuHasStockMap == null) {
                //若库存远程调用出现异常,则直接默认有库存
                skuEsModel.setHasStock(true);
            }else{
                skuEsModel.setHasStock(finalSkuHasStockMap.get(sku.getSkuId()));
            }
            //设置sku的可检索的基本属性(统一查询)
            skuEsModel.setAttrs(skuEsModelAttrs);
            BeanUtils.copyProperties(sku, skuEsModel);
            return skuEsModel;
        }).collect(Collectors.toList());

        //将要上架的商品数据发送给es进行保存
        R result = searchFeignService.productStatusUp(skuEsModels);
        if(result.getCode() == 0) {
            //商品上架成功,修改商品状态
            this.update(
                    new UpdateWrapper<SpuInfoEntity>()
                            .set("publish_status", ProductConstant.StatusEnum.SPU_UP.getCode())
                            .eq("id",spuId)
            );
        }else {
            //商品上架失败
            //TODO 重复调用问题,接口幂等性
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean down(Long spuId) {
        //1.根据spuId查询所有sku信息
        List<SkuInfoEntity> skuList = skuInfoService.getSkusBySpuId(spuId);
        //2.将所有sku信息转为id集合
        List<Long> skuIds = skuList.stream().map(sku -> {
            return sku.getSkuId();
        }).collect(Collectors.toList());
        //调用ES服务,删除对应skuId的数据
        R result = searchFeignService.productDown(skuIds);
        if(result.getCode() == 0) {
            //商品下架成功
            this.update(
                    new LambdaUpdateWrapper<SpuInfoEntity>()
                            .set(SpuInfoEntity::getPublishStatus,ProductConstant.StatusEnum.SPU_DOWN.getCode())
            );
            return true;
        }else {
            return false;
        }
    }

}