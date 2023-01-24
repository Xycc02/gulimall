package com.xuyuchao.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.common.utils.Query;
import com.xuyuchao.gulimall.product.dao.CategoryDao;
import com.xuyuchao.gulimall.product.entity.CategoryEntity;
import com.xuyuchao.gulimall.product.service.CategoryBrandRelationService;
import com.xuyuchao.gulimall.product.service.CategoryService;
import com.xuyuchao.gulimall.product.vo.Catelog2Vo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    RedissonClient redissonClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查出所有分类以及子分类,以树形结构组装
     *
     * @return
     */
    @Override
    public List<CategoryEntity> listWithTree() {
        // 1.查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);
        // 2.查询所有一级分类
        List<CategoryEntity> levelOneMenus = entities.stream().filter(item -> {
                    // 流式编程,返回集合中ParentCid为0的所有对象(一级分类对象)
                    return item.getParentCid() == 0;
                }).map(menu -> {
                    // 利用map映射设置每个父菜单的children
                    menu.setChildren(getChildrens(menu, entities));
                    return menu;
                }).sorted(Comparator.comparingInt(CategoryEntity::getSort)) // 排序(升序),比较器
                .collect(Collectors.toList());

        return levelOneMenus;
    }

    /**
     * 递归方式从总菜单中获取父菜单的子菜单
     *
     * @param category
     * @param entities
     * @return
     */
    private List<CategoryEntity> getChildrens(CategoryEntity category, List<CategoryEntity> entities) {
        List<CategoryEntity> children = entities.stream().filter(item -> {
                    return item.getParentCid().equals(category.getCatId());
                }).map(menu -> {
                    // 为每个子菜单递归生成对应的子菜单
                    menu.setChildren(getChildrens(menu, entities));
                    return menu;
                }).sorted(Comparator.comparingInt(CategoryEntity::getSort)) // 排序(升序),比较器
                .collect(Collectors.toList());

        return children;
    }

    /**
     * 批量删除分类
     *
     * @param asList
     */
    @Override
    public void removeMenuByIds(List<Long> asList) {
        // TODO 检查当前删除的菜单是否被别处引用
        baseMapper.deleteBatchIds(asList);
    }

    /**
     * 根据分类id获取分类id的全路径id
     *
     * @param catelogId
     * @return
     */
    @Override
    public Long[] findCateLogPath(Long catelogId) {
        List<Long> cateLogPath = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, cateLogPath);
        // 反转226,34,2 -> 2,34,226
        Collections.reverse(parentPath);
        // 将该list集合转为Long数组
        return parentPath.toArray(new Long[parentPath.size()]);
    }

    /**
     * 递归查找当前节点的父节点id路径  226,34,2
     *
     * @param catelogId
     * @param cateLogPath
     * @return
     */
    private List<Long> findParentPath(Long catelogId, List<Long> cateLogPath) {
        // 将该分类id放入list集合中
        cateLogPath.add(catelogId);
        // 查询当前id的分类信息
        CategoryEntity categoryEntity = this.getById(catelogId);
        if (categoryEntity.getParentCid() != 0) {
            // 递归获取父分类信息
            findParentPath(categoryEntity.getParentCid(), cateLogPath);
        }
        return cateLogPath;
    }

    /**
     * 修改分类信息,并将品牌分类表中的冗余字段更改
     *
     * @param category
     */
    @Override
    @Transactional
    public void updateDetail(CategoryEntity category) {
        // 1.根据分类id修改分类信息
        this.updateById(category);
        // 2.同步其他关联表中的数据
        if (!StringUtils.isEmpty(category.getName())) {
            categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
            // TODO 同步其他冗余字段
        }
    }

    /**
     * 查出所有的一级分类
     *
     * @return
     */
    @Override
    public List<CategoryEntity> getLevel1Categories() {
        List<CategoryEntity> categoryEntities = this.list(
                new LambdaQueryWrapper<CategoryEntity>()
                        .eq(CategoryEntity::getCatLevel, 1)
        );
        return categoryEntities;
    }

    /**
     * 查询并封装二级分类Catelog2Vo集合
     *
     * @return
     */
    @Override
    public Map<String, List<Catelog2Vo>> getCatelog2Vos() {
        // 1.从Redis中获取分类数据
        String catelogJson = stringRedisTemplate.opsForValue().get("catelogJson");
        if (StringUtils.isEmpty(catelogJson)) {
            // 2.若Redis中不存在分类数据,则查询数据库
            Map<String, List<Catelog2Vo>> map = getCatelog2VosVersion3();//三个版本,v3.0为redisson
            // 3.将数据添加到Redis中(不能在此处缓存数据,锁不住,保证不了原子性)
            // stringRedisTemplate.opsForValue().set("catelogJson", JSON.toJSONString(map),1, TimeUnit.DAYS);
            // 4.返回数据库查询的数据
            return map;
        }
        // 4.返回json序列化后指定的对象
        return JSON.parseObject(catelogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
        });
    }

    /**
     * 获取分类信息,并封装为 Map<String, List<Catelog2Vo>>
     * 优化前,吞吐量 14.6/sec
     *
     * @return
     * @version 1.0
     */
    private Map<String, List<Catelog2Vo>> getCatelog2VosVersion1() {
        // 1.获取所有一级分类
        List<CategoryEntity> level1Categories = getLevel1Categories();
        // 2.封装一级分类以及对应二级三级分类结果为Map(key为一级分类id,value为一级分类对应的List<Catelog2Vo>)
        Map<String, List<Catelog2Vo>> map = level1Categories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            // 2.1 查询一级分类id对应的二级分类
            List<CategoryEntity> category2Entities = baseMapper.selectList(
                    new QueryWrapper<CategoryEntity>()
                            .eq("parent_cid", v.getCatId())
            );
            List<Catelog2Vo> catelog2Vos = null;
            if (category2Entities != null) {
                // 2.2 将查询出来的结果封装为List<Catelog2Vo>
                catelog2Vos = category2Entities.stream().map(category2 -> {
                    // 2.3 查询出二级分类对应的三级分类信息
                    List<CategoryEntity> category3Entities = this.list(
                            new LambdaQueryWrapper<CategoryEntity>()
                                    .eq(CategoryEntity::getParentCid, category2.getCatId())
                    );
                    List<Catelog2Vo.Catelog3Vo> catelog3Vos = null;
                    if (category3Entities != null) {
                        // 2.4 将查询出来的三级分类转换为List<Catelog3Vo>
                        catelog3Vos = category3Entities.stream().map(category3 -> {
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(category2.getCatId().toString(), category3.getCatId().toString(), category3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                    }
                    Catelog2Vo catelog2Vo = new Catelog2Vo(category2.getParentCid().toString(), catelog3Vos, category2.getCatId().toString(), category2.getName());
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));
        return map;
    }


    // 初始化释放分布式锁的Lua脚本
    private static final DefaultRedisScript<Long> RELEASE_LOCK_SCRIPT;

    static {
        RELEASE_LOCK_SCRIPT = new DefaultRedisScript<>();
        RELEASE_LOCK_SCRIPT.setScriptText(
                "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                        "then\n" +
                        "    return redis.call(\"del\",KEYS[1])\n" +
                        "else\n" +
                        "    return 0\n" +
                        "end"
        );
        RELEASE_LOCK_SCRIPT.setResultType(Long.class);
    }

    /**
     * 获取分类信息,并封装为 Map<String, List<Catelog2Vo>>
     * 优化前,吞吐量 183/sec
     * >>>>>>>>>>分布式锁使用setIfAbsent + Lua脚本(释放分布式锁的原子性) <<<<<<<<<<
     *
     * @return
     * @version 2.0
     */
    private Map<String, List<Catelog2Vo>> getCatelog2VosVersion2() {

        // 获取分布式锁,并设置超时时间,防止锁得不到释放,并设置当前锁标识,防止其他线程误删
        String uuid = UUID.randomUUID().toString();
        Boolean isLock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 30, TimeUnit.SECONDS);
        if (isLock) {
            log.info(">>>>>成功获取分布式锁<<<<<");
            // 若执行业务出现异常,也需要finally释放锁
            try {
                // 获取分布式锁成功,执行获取分类信息业务

                // 此处可能有多个线程阻塞,当第一个线程获取锁并从数据库查询数据后,在此处阻塞的线程获取锁后应返回缓存中的数据(走缓存)
                String catelogJson = stringRedisTemplate.opsForValue().get("catelogJson");
                if (!StringUtils.isEmpty(catelogJson)) {
                    return JSON.parseObject(catelogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
                    });
                }

                log.info(">>>>>catelogJson查询了数据库...<<<<<");
                // 1.统一查询所有分类(避免与数据库的多次I/O)
                List<CategoryEntity> allCategories = this.list();
                // 2.获取一级分类
                List<CategoryEntity> level1Categories = allCategories.stream().filter(category1 -> {
                    return category1.getCatLevel() == 1;
                }).collect(Collectors.toList());
                // 3.将分类信息组装成 Map<String, List<Catelog2Vo>> 结构 (key为一级分类id,value为一级分类对应的List<Catelog2Vo>)
                Map<String, List<Catelog2Vo>> map = level1Categories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
                    // 4.从allCategories中过滤出一级分类id对应的二级分类
                    List<CategoryEntity> level2Categories = allCategories.stream().filter(category2 -> {
                        return category2.getParentCid() == v.getCatId();
                    }).collect(Collectors.toList());
                    List<Catelog2Vo> catelog2Vos = null;
                    // 判空,当该一级分类对应的二级分类不为空时进行组装
                    if (level2Categories != null) {
                        // 5.将二级分类组装成 List<Catelog2Vo>
                        catelog2Vos = level2Categories.stream().map(category2 -> {
                            // 6.从allCategories中过滤出二级分类id对应的三级分类
                            List<CategoryEntity> level3Categories = allCategories.stream().filter(category3 -> {
                                return category3.getParentCid() == category2.getCatId();
                            }).collect(Collectors.toList());

                            List<Catelog2Vo.Catelog3Vo> catelog3Vos = null;
                            // 判空,当该二级分类对应的三级分类不为空时进行组装
                            if (level3Categories != null) {
                                catelog3Vos = level3Categories.stream().map(category3 -> {
                                    Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(category2.getCatId().toString(), category3.getCatId().toString(), category3.getName());
                                    return catelog3Vo;
                                }).collect(Collectors.toList());
                            }
                            Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), catelog3Vos, category2.getCatId().toString(), category2.getName());
                            return catelog2Vo;
                        }).collect(Collectors.toList());
                    }
                    return catelog2Vos;
                }));
                // 7.将数据添加到Redis中(在锁释放前将数据存入Redis)
                stringRedisTemplate.opsForValue().set("catelogJson", JSON.toJSONString(map), 1, TimeUnit.DAYS);
                return map;
            } finally {
                // 8.数据添加到redis之后在释放分布式锁,只能删自己加的锁(Lua脚本保证原子性)
                /**
                 * 避免下述场景：
                 * a客户端获得的锁（键key）已经由于过期时间到了被redis服务器删除，但是这个时候a客户端还去执行DEL命令。
                 * 而b客户端已经在a设置的过期时间之后重新获取了这个同样key的锁，那么a执行DEL就会释放了b客户端加好的锁。
                 */
                // if(stringRedisTemplate.opsForValue().get("lock").equals(uuid)) {
                //     stringRedisTemplate.delete("lock");
                // }
                Long isDel = stringRedisTemplate.execute(RELEASE_LOCK_SCRIPT, Arrays.asList("lock"), uuid);
                if (isDel != 0) {
                    log.info(">>>>>释放分布式锁成功!<<<<<");
                }
            }
        }
        // 加锁失败,说明有其他线程正在重建缓存,重试
        log.info(">>>>>加分布式锁失败,准备重试...<<<<<");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return getCatelog2VosVersion2();
    }


    /**
     * 获取分类信息,并封装为 Map<String, List<Catelog2Vo>>
     * 优化前,吞吐量 183/sec
     * >>>>>>>>>>分布式锁使用Redisson <<<<<<<<<<
     *
     * @return
     * @version 3.0
     */
    private Map<String, List<Catelog2Vo>> getCatelog2VosVersion3() {

        // 获取Redisson分布式锁
        RLock lock = redissonClient.getLock("catelogJson-lock");
        lock.lock();
        log.info(">>>>>成功获取分布式锁<<<<<");
        // 若执行业务出现异常,也需要finally释放锁
        try {
            // 获取分布式锁成功,执行获取分类信息业务

            // 此处可能有多个线程阻塞,当第一个线程获取锁并从数据库查询数据后,在此处阻塞的线程获取锁后应返回缓存中的数据(走缓存)
            String catelogJson = stringRedisTemplate.opsForValue().get("catelogJson");
            if (!StringUtils.isEmpty(catelogJson)) {
                return JSON.parseObject(catelogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
                });
            }

            log.info(">>>>>catelogJson查询了数据库...<<<<<");
            // 1.统一查询所有分类(避免与数据库的多次I/O)
            List<CategoryEntity> allCategories = this.list();
            // 2.获取一级分类
            List<CategoryEntity> level1Categories = allCategories.stream().filter(category1 -> {
                return category1.getCatLevel() == 1;
            }).collect(Collectors.toList());
            // 3.将分类信息组装成 Map<String, List<Catelog2Vo>> 结构 (key为一级分类id,value为一级分类对应的List<Catelog2Vo>)
            Map<String, List<Catelog2Vo>> map = level1Categories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
                // 4.从allCategories中过滤出一级分类id对应的二级分类
                List<CategoryEntity> level2Categories = allCategories.stream().filter(category2 -> {
                    return category2.getParentCid() == v.getCatId();
                }).collect(Collectors.toList());
                List<Catelog2Vo> catelog2Vos = null;
                // 判空,当该一级分类对应的二级分类不为空时进行组装
                if (level2Categories != null) {
                    // 5.将二级分类组装成 List<Catelog2Vo>
                    catelog2Vos = level2Categories.stream().map(category2 -> {
                        // 6.从allCategories中过滤出二级分类id对应的三级分类
                        List<CategoryEntity> level3Categories = allCategories.stream().filter(category3 -> {
                            return category3.getParentCid() == category2.getCatId();
                        }).collect(Collectors.toList());

                        List<Catelog2Vo.Catelog3Vo> catelog3Vos = null;
                        // 判空,当该二级分类对应的三级分类不为空时进行组装
                        if (level3Categories != null) {
                            catelog3Vos = level3Categories.stream().map(category3 -> {
                                Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(category2.getCatId().toString(), category3.getCatId().toString(), category3.getName());
                                return catelog3Vo;
                            }).collect(Collectors.toList());
                        }
                        Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), catelog3Vos, category2.getCatId().toString(), category2.getName());
                        return catelog2Vo;
                    }).collect(Collectors.toList());
                }
                return catelog2Vos;
            }));
            // 7.将数据添加到Redis中(在锁释放前将数据存入Redis)
            stringRedisTemplate.opsForValue().set("catelogJson", JSON.toJSONString(map), 1, TimeUnit.DAYS);
            return map;
        } finally {
            // 8.数据添加到redis之后在释放分布式锁
            lock.unlock();
            log.info(">>>>>释放分布式锁成功!<<<<<");
        }
    }
}