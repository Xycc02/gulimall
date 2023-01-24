package com.xuyuchao.gulimall.ware.feign;

import com.xuyuchao.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: xuyuchao
 * @Date: 2022-08-13-11:23
 * @Description:
 */
@FeignClient("gulimall-product")
public interface ProductFeignService {
    //根据skuId获取sku商品信息
    @RequestMapping("product/skuinfo/info/{skuId}")
    R info(@PathVariable("skuId") Long skuId);
}
