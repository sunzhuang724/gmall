package com.atguigu.gmall.index.config;

import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.index.feign.GmallPmsClient;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class BloomFilterConfig {
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private GmallPmsClient pmsClient;

    @Bean
    public RBloomFilter rBloomFilter(){
//        布隆过滤器的初始化，及其相关配置
        RBloomFilter<Object> bloomfilter = redissonClient.getBloomFilter("bloomfilter");
//        设置过滤器字段的长度，与精确性，精确性是要牺牲一些空间和性能
        bloomfilter.tryInit(501,0.03);
        ResponseVo<List<CategoryEntity>> listResponseVo = pmsClient.queryCategoryByPid(0l);
        List<CategoryEntity> categoryEntities = listResponseVo.getData();
        if (!CollectionUtils.isEmpty(categoryEntities)){
            categoryEntities.forEach(categoryEntity -> {
                bloomfilter.add(categoryEntity.getId().toString());
            });
        }
        return bloomfilter;
    }

}
