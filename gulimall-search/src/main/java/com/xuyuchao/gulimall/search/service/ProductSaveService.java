package com.xuyuchao.gulimall.search.service;

import com.xuyuchao.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @Author: xuyuchao
 * @Date: 2022-11-17-15:17
 * @Description:
 */
public interface ProductSaveService {
    //商品上架
    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
