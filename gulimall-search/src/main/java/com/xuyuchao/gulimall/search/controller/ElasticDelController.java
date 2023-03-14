package com.xuyuchao.gulimall.search.controller;

import com.xuyuchao.common.exception.BizCodeEnum;
import com.xuyuchao.common.utils.R;
import com.xuyuchao.gulimall.search.service.ProductDelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description: ElasticDelController
 * @Author XYC
 * @Date: 2023/3/14 11:04
 * @Version 1.0
 */

@Slf4j
@RequestMapping("/search/down")
@RestController
public class ElasticDelController {

    @Autowired
    ProductDelService productDelService;


    /**
     * 商品下架
     * @return
     */
    @PostMapping("/product")
    public R productDown(@RequestBody List<Long> skuIds) {
        boolean isSuccess = false;
        isSuccess = productDelService.productDel(skuIds);
        if(isSuccess) {
            return R.ok();
        }else {
            return R.error(BizCodeEnum.PRODUCT_DOWN_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_DOWN_EXCEPTION.getMsg());
        }
    }
}
