package com.xuyuchao.common.validator.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: xuyuchao
 * @Date: 2022-07-24-23:21
 * @Description: 自定义数据校验器(与自定义校验注解绑定)
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue,Integer> {

    //自定义的属性值
    private Set<Integer> set = new HashSet<>();

    /**
     * 初始化方法
     * @param constraintAnnotation
     */
    @Override
    public void initialize(ListValue constraintAnnotation) {
        //获取自定义属性的值,@ListValue(values={0,1})
        int[] values = constraintAnnotation.values();
        for(int value : values) {
            set.add(value);
        }
    }

    /**
     * 判断是否校验成功
     * @param integer
     * @param constraintValidatorContext
     * @return
     */
    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        //integer参数为发送过来需要校验的值
        if(set.contains(integer)) {
            //校验成功
            return true;
        }
        return false;
    }
}
