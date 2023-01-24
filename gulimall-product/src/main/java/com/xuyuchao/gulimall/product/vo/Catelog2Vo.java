package com.xuyuchao.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: Catelog2Vo
 * @Author XYC
 * @Date: 2022/11/20 20:56
 * @Version 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Catelog2Vo {
    private String catalog1Id;//1级父分类id
    private List<Catelog3Vo> catalog3List;//3级子分类集合
    private String id;
    private String name;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Catelog3Vo {
        private String catalog2Id;//2级父分类id
        private String id;
        private String name;
    }
}
