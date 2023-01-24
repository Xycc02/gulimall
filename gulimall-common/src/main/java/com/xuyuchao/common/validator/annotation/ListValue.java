package com.xuyuchao.common.validator.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @Author: xuyuchao
 * @Date: 2022-07-24-23:12
 * @Description: 自定义校验注解@ListValue(vals={0,1})
 */
@Documented
@Constraint(
        validatedBy = {ListValueConstraintValidator.class}//指定自定义校验器
)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ListValue {
    String message() default "{com.xuyuchao.common.validator.annotation.ListValue.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    //自定义属性
    int[] values() default {};
}
