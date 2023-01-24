package com.xuyuchao.gulimall.product.exception;

import com.xuyuchao.common.exception.BizCodeEnum;
import com.xuyuchao.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: xuyuchao
 * @Date: 2022-07-24-21:53
 * @Description: 全局异常处理器
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.xuyuchao.gulimall.product.controller")
public class GulimallExceptionControllerAdvice {
    /**
     * 数据校验异常处理器
     * @param e
     * @return
     */
    @ExceptionHandler(value= MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e) {
        log.error("数据校验错误:{},异常类型:{}",e.getMessage(),e.getClass());
        Map<String,String> errors = new HashMap<>();
        BindingResult bindingResult = e.getBindingResult();
        bindingResult.getFieldErrors().forEach(item -> {
            //获取每个错误结果的属性名
            String fieldName = item.getField();
            //获取每个属性名的错误提示(可在实体属性上自定义@NotBlank(message = "品牌名不能为空"))
            String message = item.getDefaultMessage();
            errors.put(fieldName,message);
        });
        return R.error(BizCodeEnum.VALID_EXCEPTION.getCode(),BizCodeEnum.VALID_EXCEPTION.getMsg()).put("data",errors);
    }

    /**
     * 异常兜底处理器
     * @param throwable
     * @return
     */
    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable throwable) {
        log.error("异常兜底处理器:{}",throwable.getMessage());
        return R.error(BizCodeEnum.UNKNOW_EXCEPTION.getCode(),BizCodeEnum.UNKNOW_EXCEPTION.getMsg());
    }
}
