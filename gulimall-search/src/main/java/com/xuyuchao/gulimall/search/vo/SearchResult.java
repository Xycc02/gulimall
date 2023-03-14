package com.xuyuchao.gulimall.search.vo;

import com.xuyuchao.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.List;

/**
 * @Description: 封装了前端页面检索商品后返回给页面的信息
 * @Author XYC
 * @Date: 2023/3/13 20:35
 * @Version 1.0
 */
@Data
public class SearchResult {
    private List<SkuEsModel> products; //商品数据


    private Integer pageNum;    //当前页
    private Long total;         //总记录数
    private Integer totalPages;  //总页数


    private List<BrandVo> brands;  //当前查询的结果涉及的所有品牌信息
    private List<AttrVo> attrs;    //当前查询结果涉及的所有属性信息
    private List<CatalogVo> catalogs;   //当前查询结果涉及的所有分类信息


    @Data
    public static class BrandVo {
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class AttrVo {
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }

    @Data
    public static class CatalogVo {
        private Long catalogId;
        private String catalogName;
    }

}
