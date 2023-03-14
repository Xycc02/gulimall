package com.xuyuchao.gulimall.search.service;

import java.util.List;

public interface ProductDelService {
    //根据skuid下架商品
    boolean productDel(List<Long> skuIds);
}
