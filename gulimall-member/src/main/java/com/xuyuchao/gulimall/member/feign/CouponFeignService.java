package com.xuyuchao.gulimall.member.feign;

import com.xuyuchao.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Author: xuyuchao
 * @Date: 2022-07-19-22:39
 * @Description:
 */
@Component
@FeignClient("gulimall-coupon")
public interface CouponFeignService {
    @GetMapping("/coupon/coupon/member/list")
    public R memberCoupons();
}
