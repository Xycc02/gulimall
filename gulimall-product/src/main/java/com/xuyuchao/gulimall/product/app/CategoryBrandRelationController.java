package com.xuyuchao.gulimall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuyuchao.gulimall.product.entity.BrandEntity;
import com.xuyuchao.gulimall.product.vo.BrandVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.xuyuchao.gulimall.product.entity.CategoryBrandRelationEntity;
import com.xuyuchao.gulimall.product.service.CategoryBrandRelationService;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.common.utils.R;



/**
 * 品牌分类关联
 *
 * @author xuyuchao
 * @email 2672424338@qq.com
 * @date 2022-07-19 00:10:37
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;


    /**
     * 获取当前品牌id的所有分类信息(分类id分类名)
     */
    @GetMapping("/catelog/list")
    public R catelogList(@RequestParam("brandId") Long brandId){
        List<CategoryBrandRelationEntity> data = categoryBrandRelationService.list(new LambdaQueryWrapper<CategoryBrandRelationEntity>()
                .eq(CategoryBrandRelationEntity::getBrandId, brandId));

        return R.ok().put("data", data);
    }

    /**
     * 根据商品分类id获取所有对应品牌信息
     * @param catId
     * @return
     */
    @GetMapping("/brands/list")
    public R relationBrands(@RequestParam(value = "catId",required = true) Long catId) {
        List<BrandEntity> brandEntities = categoryBrandRelationService.getBrandsByCatId(catId);
        if(brandEntities != null && brandEntities.size() > 0) {
            //封装vo对象
            List<BrandVo> data = brandEntities.stream().map(item -> {
                BrandVo brandVo = new BrandVo();
                brandVo.setBrandId(item.getBrandId());
                brandVo.setBrandName(item.getName());
                return brandVo;
            }).collect(Collectors.toList());
            return R.ok().put("data",data);
        }
        return R.ok().put("data",null);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
        public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
        public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
        public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){

		categoryBrandRelationService.saveDetail(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
        public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
        public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
