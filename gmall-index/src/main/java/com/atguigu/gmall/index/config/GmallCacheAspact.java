package com.atguigu.gmall.index.config;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

//代理实现类，作用是将添加注解的目标方法实现自定的增强（按照注解想形成的模板功能）
/*
* 增强的方法有多种
* 1.@Around
* 2.@Before
* 3.@AfterReturning
* 4.@AfterThrowing
* 5.@After
*根据我们分布式锁的业务逻辑这里选择的是环绕增强
* */
@Aspect
@Component
public class GmallCacheAspact {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private RBloomFilter rBloomFilter;

    @Around("@annotation(GmallCache)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
//        环绕前通知
//      添加布隆过滤器
        List<Object> args = Arrays.asList(joinPoint.getArgs());
        String pid = args.get(0).toString();
        if (!this.rBloomFilter.contains(pid)){
            return null;
        }

//    获取目标方法对象
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
//     获取目标方法的注解
        GmallCache gmallCache = method.getAnnotation(GmallCache.class);
        String prefix = gmallCache.prefix();
//        获取目标方法的返回类型
        Class<?> returnType = method.getReturnType();
//        获取目标方法的参数列表
//                  方法对象没有直接获取参数的方法
        Object[] Arrayargs = joinPoint.getArgs();
//        List<Object> args = Arrays.asList(Arrayargs);
        String key = prefix + args;
//        先查询缓存，若有的话直接直接反序列化返回
        String json = redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(json)){
            return JSON.parseObject(json,returnType);
        }
//        防止缓存被击穿添加分布式锁
        String lock = gmallCache.lock();//注解接口中的属性值
        RLock fairLock = redissonClient.getFairLock(lock + args);
        fairLock.lock();
        try {
//          再查缓存，若有的话直接直接反序列化返回
            String json2 = redisTemplate.opsForValue().get(key);
            if (StringUtils.isNotBlank(json2)){
                return JSON.parseObject(json2,returnType);
            }
//        执行目标方法,获取数据库中的数据
            Object result = joinPoint.proceed(joinPoint.getArgs());
//        环绕后通知
//          如果缓存result值为空，为了防止缓存穿透，保存null值，但值给与段时间的保存
            if (result == null){
                redisTemplate.opsForValue().set(key,null,1, TimeUnit.MINUTES);
            }else {
    //            防止缓存血崩，要给缓存添加随机值的保存时间
                long timeout = gmallCache.timeout() + new Random().nextInt(gmallCache.random());
                redisTemplate.opsForValue().set(key,JSON.toJSONString(result),timeout,TimeUnit.MINUTES);
            }
            return result;
        } finally {
            fairLock.unlock();
        }
    }
}
