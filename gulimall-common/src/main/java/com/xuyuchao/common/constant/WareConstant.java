package com.xuyuchao.common.constant;

import lombok.Data;

/**
 * @Author: xuyuchao
 * @Date: 2022-08-09-21:27
 * @Description:
 */
@Data
public class WareConstant {
    /**
     * 采购需求状态枚举类
     */
    public enum PurchaseDetailEnum {
        CREATED(0,"新建"),
        ASSIGNED(1,"已分配"),
        BUYING(2,"正在采购"),
        FINISHED(3,"已完成"),
        ERROR(4,"采购失败");

        private int code;
        private String type;

        PurchaseDetailEnum(int code,String type) {
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

    /**
     * 采购单状态枚举类
     */
    public enum PurchaseStatusEnum {
        CREATED(0,"新建"),
        ASSIGNED(1,"已分配"),
        RECEIVED(2,"已领取"),
        FINISHED(3,"已完成"),
        ERROR(4,"有异常");

        private int code;
        private String type;

        PurchaseStatusEnum(int code,String type) {
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
}
