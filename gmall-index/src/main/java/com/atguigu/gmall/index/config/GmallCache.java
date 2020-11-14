package com.atguigu.gmall.index.config;

import java.lang.annotation.*;

//自定义的注解接口，上面的三个注解是所有注解类都有的
@Target({ElementType.METHOD})//作用域方法
@Retention(RetentionPolicy.RUNTIME)//运行时注解
@Documented
public @interface GmallCache {
    String prefix() default "gmall:cache";//缓存key的前缀

    long timeout() default 5l;

    int random() default 5;

    String lock() default "lock";
}
