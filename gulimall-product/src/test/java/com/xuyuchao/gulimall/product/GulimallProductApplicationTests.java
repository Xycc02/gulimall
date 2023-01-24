package com.xuyuchao.gulimall.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xuyuchao.gulimall.product.entity.BrandEntity;
import com.xuyuchao.gulimall.product.service.BrandService;
import com.xuyuchao.gulimall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
@Slf4j
// @RunWith(SpringRunner.class)
public class GulimallProductApplicationTests {

    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    /**
     * 测试Redisson
     */
    @Test
    void testRedisson() {
        System.out.println(redissonClient);
    }

    /**
     * 测试Redis
     */
    @Test
    void testRedis() {
        stringRedisTemplate.opsForValue().set("name","xuyuchao");

        String name = stringRedisTemplate.opsForValue().get("name");
        System.out.println(name);
    }

    /**
     * 测试插入数据
     */
    @Test
    public void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setName("华为");
        boolean res = brandService.save(brandEntity);
        if(res) {
            System.out.println("保存成功!");
        }
    }

    /**
     * 测试查询数据
     */
    @Test
    public void test1() {
        QueryWrapper<BrandEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name","华为");
        BrandEntity brandEntity = brandService.getOne(queryWrapper);
        System.out.println(brandEntity);
    }

    /**
     * 测试根据分类id获取分类id路径 (2,34,226) 用于前端回显级联选择器
     */
    @Test
    public void test2() {
        Long[] cateLogPath = categoryService.findCateLogPath(226L);
        log.error("分类id完整路径{}", Arrays.asList(cateLogPath));
    }

    /**
     * List转Map
     */
    @Test
    void testListConvertMap() {
        List<TestClass> list = new ArrayList<>();
        list.add(new TestClass(43L,true));
        list.add(new TestClass(44L,false));
        list.add(new TestClass(45L,true));
        list.add(new TestClass(46L,false));

        Map<Long, Boolean> map = list.stream().collect(Collectors.toMap(TestClass::getSkuId, TestClass::isHasStock));
        System.out.println(map);



        Map<String,Object> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("skuId",43);
        linkedHashMap.put("hasStock",true);
    }


    class TestClass {
        private Long skuId;
        private boolean hasStock;

        public TestClass(Long skuId, boolean hasStock) {
            this.skuId = skuId;
            this.hasStock = hasStock;
        }

        public Long getSkuId() {
            return skuId;
        }

        public void setSkuId(Long skuId) {
            this.skuId = skuId;
        }

        public boolean isHasStock() {
            return hasStock;
        }

        public void setHasStock(boolean hasStock) {
            this.hasStock = hasStock;
        }
    }
}
