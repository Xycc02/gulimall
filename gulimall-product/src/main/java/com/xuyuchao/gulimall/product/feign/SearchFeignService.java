package com.xuyuchao.gulimall.product.feign;

import com.xuyuchao.common.to.es.SkuEsModel;
import com.xuyuchao.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("gulimall-search")
public interface SearchFeignService {
    @PostMapping("/search/save/product")
    R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels);

    @PostMapping("/search/down/product")
    R productDown(@RequestBody List<Long> skuIds);
}
