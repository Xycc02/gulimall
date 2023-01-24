package com.xuyuchao.gulimall.product.app;

import java.util.Arrays;
import java.util.Map;

import com.xuyuchao.common.validator.group.AddGroup;
import com.xuyuchao.common.validator.group.UpdateGroup;
import com.xuyuchao.common.validator.group.UpdateStatusGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.xuyuchao.gulimall.product.entity.BrandEntity;
import com.xuyuchao.gulimall.product.service.BrandService;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.common.utils.R;


/**
 * 品牌
 *
 * @author xuyuchao
 * @email 2672424338@qq.com
 * @date 2022-07-19 00:10:37
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
        public R list(@RequestParam Map<String, Object> params){

        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
        public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     * @Valid   数据校验
     * @Validated   数据校验,校验分组
     * BindingResult result 校验结果封装(校验失败直接抛给全局异常处理器处理)
     */
    @RequestMapping("/save")
        public R save(@Validated({AddGroup.class}) @RequestBody BrandEntity brand/*,BindingResult result*/){
        // if(result.hasErrors()) {
        //     //校验错误
        //     //1.封装校验的错误信息
        //     Map<String,String> errors = new HashMap<>();
        //     //2.获取校验的错误结果List<FieldError>
        //     result.getFieldErrors().forEach(item -> {
        //         //获取每个错误结果的属性名
        //         String fieldName = item.getField();
        //         //获取每个属性名的错误提示(可在实体属性上自定义@NotBlank(message = "品牌名不能为空"))
        //         String message = item.getDefaultMessage();
        //         errors.put(fieldName,message);
        //     });
        //     return R.error(400,"提交的数据不合法").put("data",errors);
        // }else {
        //
        // }
        //校验正确
        brandService.save(brand);
        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
        public R update(@Validated({UpdateGroup.class}) @RequestBody BrandEntity brand){
        //修改品牌信息以及关联的品牌分类表中的冗余字段
		brandService.updateDetail(brand);

        return R.ok();
    }

    /**
     * 修改品牌状态
     */
    @PostMapping("/updateStatus")
    public R updateStatus(@Validated({UpdateStatusGroup.class}) @RequestBody BrandEntity brand){
        brandService.updateById(brand);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
        public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
