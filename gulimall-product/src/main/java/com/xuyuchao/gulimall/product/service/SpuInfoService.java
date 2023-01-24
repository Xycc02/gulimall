package com.xuyuchao.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.gulimall.product.entity.SpuInfoEntity;
import com.xuyuchao.gulimall.product.vo.spu.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author xuyuchao
 * @email 2672424338@qq.com
 * @date 2022-07-18 23:37:13
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
    //保存商品信息
    void saveSpuInfo(SpuSaveVo spuSaveVo);
    //1.保存spu的基本信息(pms_spu_info)
    void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity);
    //根据条件检索spu信息
    PageUtils queryPageByCondition(Map<String, Object> params);
    //商品上架(保存到es)
    void up(Long spuId);
}

