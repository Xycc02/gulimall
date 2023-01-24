package com.xuyuchao.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xuyuchao.gulimall.product.entity.BrandEntity;
import com.xuyuchao.gulimall.product.entity.CategoryEntity;
import com.xuyuchao.gulimall.product.service.BrandService;
import com.xuyuchao.gulimall.product.service.CategoryService;
import com.xuyuchao.gulimall.product.vo.BrandVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.common.utils.Query;

import com.xuyuchao.gulimall.product.dao.CategoryBrandRelationDao;
import com.xuyuchao.gulimall.product.entity.CategoryBrandRelationEntity;
import com.xuyuchao.gulimall.product.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 保存品牌和分类的关系,前端只传过来品牌id和分类id,我们将根据这两个数据从表中获得分类名和品牌名存入该表,避免大表关联查询,影响数据库性能
     * (注意冗余字段一致性)
     * @param categoryBrandRelation
     */
    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        //根据前端传过来的品牌id和分类id获取品牌信息和分类信息
        BrandEntity brand = brandService.getById(categoryBrandRelation.getBrandId());
        CategoryEntity category = categoryService.getById(categoryBrandRelation.getCatelogId());
        //将品牌名和分类名放进categoryBrandRelation实体类并保存到数据库
        categoryBrandRelation.setBrandName(brand.getName());
        categoryBrandRelation.setCatelogName(category.getName());
        //保存
        this.save(categoryBrandRelation);
    }

    /**
     * 根据品牌id修改品牌名
     * @param brandId
     * @param name
     */
    @Override
    public void updateBrand(Long brandId, String name) {
        LambdaUpdateWrapper<CategoryBrandRelationEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(CategoryBrandRelationEntity::getBrandName,name)
                .eq(CategoryBrandRelationEntity::getBrandId,brandId);
        this.update(updateWrapper);
    }

    /**
     * 根据分类id修改分类名
     * @param catId
     * @param name
     */
    @Override
    public void updateCategory(Long catId, String name) {
        LambdaUpdateWrapper<CategoryBrandRelationEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(CategoryBrandRelationEntity::getCatelogName,name)
                .eq(CategoryBrandRelationEntity::getCatelogId,catId);
        this.update(updateWrapper);
    }

    /**
     * 根据商品分类id获取所有对应品牌信息
     * @param catId
     * @return
     */
    @Override
    public List<BrandEntity> getBrandsByCatId(Long catId) {
        //1.在关系表中根据分类id获取所有品牌id
        List<CategoryBrandRelationEntity> relationEntities = this.list(
                new LambdaQueryWrapper<CategoryBrandRelationEntity>()
                        .eq(CategoryBrandRelationEntity::getCatelogId, catId)
        );
        if(relationEntities.size() > 0) {
            //获取所有品牌id
            List<Long> brandIds = relationEntities.stream().map(CategoryBrandRelationEntity::getBrandId).collect(Collectors.toList());
            //根据品牌id获取品牌信息
            List<BrandEntity> brandEntities = (List<BrandEntity>) brandService.listByIds(brandIds);
            return brandEntities;
        }
        return null;
    }
}