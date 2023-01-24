package com.xuyuchao.gulimall.product.feign;

import com.xuyuchao.common.to.SkuReductionTo;
import com.xuyuchao.common.to.SpuBoundsTo;
import com.xuyuchao.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: xuyuchao
 * @Date: 2022-08-04-22:16
 * @Description:
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService {

    //保存spu积分信息
    @RequestMapping("coupon/smsspubounds/saveSpuBounds")
    R saveSpuBounds(@RequestBody SpuBoundsTo spuBoundsTo);

    //保存sku满减信息
    @RequestMapping("coupon/skufullreduction/saveSkuReduction")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
