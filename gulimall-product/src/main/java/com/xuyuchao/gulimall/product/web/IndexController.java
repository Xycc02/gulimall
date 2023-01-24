package com.xuyuchao.gulimall.product.web;

import com.xuyuchao.gulimall.product.entity.CategoryEntity;
import com.xuyuchao.gulimall.product.service.CategoryService;
import com.xuyuchao.gulimall.product.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description: IndexController
 * @Author XYC
 * @Date: 2022/11/20 19:52
 * @Version 1.0
 */
@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;
    @Autowired
    RedissonClient redissonClient;

    @GetMapping({"/","/index.html"})
    public String indexPage(Model model) {
        //1.查出所有的一级分类
        List<CategoryEntity> categoryEntities = categoryService.getLevel1Categories();
        model.addAttribute("categories",categoryEntities);
        return "index";
    }

    /**
     * 查询并封装二级分类Catelog2Vo集合
     * @return
     */
    @ResponseBody
    @GetMapping("index/catalog.json")
    public Map<String,List<Catelog2Vo>> getCatelog2Vos() {
        Map<String,List<Catelog2Vo>> map = categoryService.getCatelog2Vos();
        return map;
    }

    @ResponseBody
    @GetMapping("testRedisson")
    public String testRedisson() {
        //1.获得分布式锁
        RLock lock = redissonClient.getLock("my-lock");
        //2.加锁
        try {
            // lock.lock(); //不加超时时间,看门狗机制,释放锁时间30s,并在业务未执行完时,每10s续期满
            lock.lock(30, TimeUnit.SECONDS); //手动设置释放锁时间,并手动解锁
            System.out.println("加锁成功!执行业务..." + Thread.currentThread().getId());
            Thread.sleep(30000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            //3.解锁
            System.out.println("释放锁..." + Thread.currentThread().getId());
            lock.unlock();
        }
        return "testRedisson";
    }

}
