package com.atguigu.gmall.index.service;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.index.config.GmallCache;
import com.atguigu.gmall.index.feign.GmallPmsClient;
import com.atguigu.gmall.pms.api.GmallPmsApi;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class IndexService {
    @Autowired
    private GmallPmsClient pmsClient;
//    封装过RedisTemplate,这种都是用string类型来读写的
    @Autowired
    private StringRedisTemplate redisTemplate;

//    private static final String KEY_PREFIX = "index:cates:";

    public List<CategoryEntity> queryLel1Categories() {
        ResponseVo<List<CategoryEntity>> listResponseVo = this.pmsClient.queryCategoryByPid(0L);
        System.out.println("listResponseVo的内容"+listResponseVo);
        List<CategoryEntity> categoryEntities = listResponseVo.getData();
        return categoryEntities;
    }

//获取二级种类与三级种类，需要将该业务添加缓存
    @GmallCache(prefix = "index:cates:",timeout = 14400,random = 3600,lock = "lock")
    public List<CategoryEntity> queryLvl2CatesWithSubsByPid(Long pid) {
//        String json = redisTemplate.opsForValue().get(KEY_PREFIX + pid);
////        此处json是json格式的集合
//        System.out.println("==1.===json对象======"+json);
//        if (StringUtils.isNotBlank(json)){
//            List<CategoryEntity> categoryEntities = JSON.parseArray(json, CategoryEntity.class);
////            这里是CategoryEntity类型的集合
//            System.out.println("======2.categoryEntities========="+categoryEntities);
//            return categoryEntities;
//        }
        ResponseVo<List<CategoryEntity>> listResponseVo = pmsClient.queryLvl2CatesWithSubsByPid(pid);
        List<CategoryEntity> categoryEntities = listResponseVo.getData();

//        if (CollectionUtils.isEmpty(categoryEntities)){
//            // 为了防止缓存穿透，数据即使为null页缓存，为了防止缓存数据过多，缓存时间设置的极短
//            this.redisTemplate.opsForValue().set(KEY_PREFIX + pid, JSON.toJSONString(categoryEntities), 1, TimeUnit.MINUTES);
//        } else {
//            // 为了防止缓存雪崩，给缓存时间添加随机值，key是string，value是json格式
//            this.redisTemplate.opsForValue().set(KEY_PREFIX + pid, JSON.toJSONString(categoryEntities), 2160 + new Random().nextInt(900), TimeUnit.HOURS);
//        }
        return  categoryEntities;
    }
}
