package com.xuyuchao.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuyuchao.common.to.es.SkuEsModel;
import com.xuyuchao.gulimall.search.config.GulimallESConfig;
import com.xuyuchao.gulimall.search.constant.EsConstant;
import com.xuyuchao.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: xuyuchao
 * @Date: 2022-11-17-15:20
 * @Description:
 */
@Slf4j
@Service("productSaveService")
public class ProductSaveServiceImpl implements ProductSaveService {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    /**
     * 将sku商品数据保存到ES中
     * @param skuEsModels
     */
    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {
        //保存到ES
        //1.用kibana建立好product索引
        //2.批量保存sku商品信息
        BulkRequest bulkRequest = new BulkRequest(EsConstant.PRODUCT_INDEX);
        skuEsModels.forEach(skuEsModel -> {
            IndexRequest indexRequest = new IndexRequest();
            indexRequest.id(skuEsModel.getSkuId().toString());
            indexRequest.source(JSON.toJSONString(skuEsModel), XContentType.JSON);
            bulkRequest.add(indexRequest);
        });
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, GulimallESConfig.COMMON_OPTIONS);
        //若批量处理错误
        if(bulk.hasFailures()) {
            List<String> failIds = Arrays.stream(bulk.getItems()).map(item -> {
                return item.getId();
            }).collect(Collectors.toList());
            log.error("批量保存商品信息错误,错误商品ID:{}",failIds);
        }
        return !bulk.hasFailures();
    }
}
