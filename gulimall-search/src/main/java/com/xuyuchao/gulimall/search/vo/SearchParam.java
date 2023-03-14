package com.xuyuchao.gulimall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description: 封装页面所有可能传递过来的检索条件
 * @Author XYC
 * @Date: 2023/3/13 16:15
 * @Version 1.0
 */

@Data
public class SearchParam {
    private String keyword; //检索关键字
    private Long catalog3Id; //检索商品三级分类ID

    /**
     * 销量,价格,热度评分(升降序)
     */
    private String sort; //排序条件
    private Integer hasStock; //是否只显示有货商品过滤 0/1
    private String skuPrice;  //价格区间过滤  500_1000
    private List<Long> brandId;  //根据品牌ID进行过滤,可以多选
    private List<String> attrs;  //根据商品属性进行筛选,可以多选 1_安卓:苹果&2_...
    private Integer pageNum;  //页码
}
