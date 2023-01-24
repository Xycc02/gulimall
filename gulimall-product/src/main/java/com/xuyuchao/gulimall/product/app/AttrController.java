package com.xuyuchao.gulimall.product.app;

import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.common.utils.R;
import com.xuyuchao.gulimall.product.entity.ProductAttrValueEntity;
import com.xuyuchao.gulimall.product.service.AttrService;
import com.xuyuchao.gulimall.product.vo.AttrRespVo;
import com.xuyuchao.gulimall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * 商品属性
 *
 * @author xuyuchao
 * @email 2672424338@qq.com
 * @date 2022-07-28 23:47:56
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    /**
     * 列表
     */
    @RequestMapping("/list")
        public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 根据spuId修改pu规格信息
     * @param spuId
     * @return
     */
    @PostMapping("/update/{spuId}")
    public R updateBaseAttrById(@PathVariable Long spuId,
                                @RequestBody List<ProductAttrValueEntity> baseAttrs) {
        return attrService.updateBaseAttrById(spuId,baseAttrs);
    }
    /**
     * 根据spuId获取spu规格信息
     * @param spuId
     * @return
     */
    @GetMapping("/base/listforspu/{spuId}")
    public R getBaseAttrById(@PathVariable Long spuId) {
        return attrService.getBaseAttrById(spuId);
    }
    /**
     * 根据分类id获取对应属性信息(由路径传来销售属性或者基本属性  sale-销售属性，base-基本属性)
     * @param catelogId
     * @return
     */
    @GetMapping("/{attrType}/list/{catelogId}")
    public R baseList(@RequestParam Map<String, Object> params
            ,@PathVariable Long catelogId
            ,@PathVariable String attrType) {
        PageUtils page = attrService.queryAttrPage(params,catelogId,attrType);
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
        public R info(@PathVariable("attrId") Long attrId){
		// AttrEntity attr = attrService.getById(attrId);
        AttrRespVo attr = attrService.getAttrInfo(attrId);

        return R.ok().put("attr", attr);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
        public R save(@RequestBody AttrVo attr){
		attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
        public R update(@RequestBody AttrVo attr){
		attrService.updateAttr(attr);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
        public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));
        //TODO 删除基本属性也将属性和属性分组关联记录删除掉,不删也无妨

        return R.ok();
    }

}
