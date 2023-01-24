package com.xuyuchao.gulimall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.xuyuchao.gulimall.product.entity.AttrEntity;
import com.xuyuchao.gulimall.product.service.AttrAttrgroupRelationService;
import com.xuyuchao.gulimall.product.service.AttrService;
import com.xuyuchao.gulimall.product.service.CategoryService;
import com.xuyuchao.gulimall.product.vo.AttrGroupRelationVo;
import com.xuyuchao.gulimall.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.xuyuchao.gulimall.product.entity.AttrGroupEntity;
import com.xuyuchao.gulimall.product.service.AttrGroupService;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.common.utils.R;



/**
 * 属性分组
 *
 * @author xuyuchao
 * @email 2672424338@qq.com
 * @date 2022-07-19 00:10:38
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttrService attrService;
    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
        public R list(@RequestParam Map<String, Object> params,@PathVariable Long catelogId){
        PageUtils page = attrGroupService.queryPage(params,catelogId);

        return R.ok().put("page", page);
    }

    /**
     * 根据分类id获取属性分组以及分组所对应属性
     * @param catelogId
     * @return
     */
    @GetMapping("/{catelogId}/withattr")
    public R groupWithAttr(@PathVariable Long catelogId) {
        List<AttrGroupWithAttrsVo> data = attrGroupService.getGroupWithAttrByCatelogId(catelogId);
        return R.ok().put("data",data);
    }
    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
        public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        //根据分类id获取属性分组的分类路径
        Long catelogId = attrGroup.getCatelogId();
        Long[] catelogPath = categoryService.findCateLogPath(catelogId);
        attrGroup.setCatelogPath(catelogPath);

        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
        public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
        public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
        public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

    /**
     * 根据属性分组id获取对应属性
     * @param groupId
     * @return
     */
    @GetMapping("/{groupId}/attr/relation")
    public R attrRelation(@PathVariable Long groupId) {
        List<AttrEntity> data = attrService.getRelationAttr(groupId);
        return R.ok().put("data",data);
    }

    /**
     * 查出分组id对应分类的属性,且该属性未关联过其他分组
     * @param params
     * @param groupId
     * @return
     */
    @RequestMapping("/{groupId}/noattr/relation")
    public R attrNoRelation(@RequestParam Map<String, Object> params,@PathVariable Long groupId){
        // PageUtils page = attrGroupService.getAttrList(params,catelogId);
        PageUtils page = attrService.getNoRelationAttr(params,groupId);
        return R.ok().put("page", page);
    }

    /**
     * 删除属性和属性分组关系
     * @param relationVos
     * @return
     */
    @PostMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody AttrGroupRelationVo[] relationVos) {
        System.out.println("relationVos:" + Arrays.toString(relationVos));
        attrGroupService.deleteRelation(relationVos);
        return R.ok();
    }

    /**
     * 添加属性和属性分组关系
     * @param relationVos
     * @return
     */
    @PostMapping("/attr/relation")
    public R saveRelation(@RequestBody List<AttrGroupRelationVo> relationVos) {
        attrAttrgroupRelationService.saveRelation(relationVos);
        return R.ok();
    }

}
