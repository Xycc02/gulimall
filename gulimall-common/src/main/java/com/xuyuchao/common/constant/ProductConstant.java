package com.xuyuchao.common.constant;

/**
 * @Author: xuyuchao
 * @Date: 2022-07-30-23:23
 * @Description:
 */
public class ProductConstant {
    /**
     * 属性分类枚举类 (sale-销售属性-0，base-基本属性-1)
     */
    public enum AttrEnum {
        ATTR_TYPE_BASE(1,"base"),
        ATTR_TYPE_SALE(0,"sale");

        private int code;//销售属性-0，基本属性-1
        private String type;//属性分类名

        AttrEnum(int code,String type) {
            this.code = code;
            this.type = type;
        }

        public int getCode() {
            return code;
        }

        public String getType() {
            return type;
        }
    }

    public enum StatusEnum {
        SPU_NEW(0,"新建"),
        SPU_UP(1,"商品上架"),
        SPU_DOWN(2,"商品下架");

        StatusEnum(int code, String type) {
            this.code = code;
            this.type = type;
        }

        private int code;
        private String type;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
