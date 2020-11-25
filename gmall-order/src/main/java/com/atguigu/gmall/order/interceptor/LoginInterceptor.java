package com.atguigu.gmall.order.interceptor;

import com.atguigu.gmall.cart.config.JwtProperties;
import com.atguigu.gmall.cart.pojo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Component
@EnableConfigurationProperties(JwtProperties.class)
public class LoginInterceptor implements HandlerInterceptor {
   // public String userId;
    @Autowired
    private JwtProperties properties;

    private static final ThreadLocal<UserInfo> THREAD_LOCAL = new ThreadLocal<>();
//在进入系统（购物车）之前拦截，获取登录信息
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        System.out.println("这里是一个拦截器");
        THREAD_LOCAL.set(new UserInfo(111l, UUID.randomUUID().toString()));
        return true;
    }
//给外面一个获得thread_local的方法，避免直接得到再其他地方更改
    public static UserInfo getUserInfo(){
//        在底层ThreadLocal里有一个ThreadLocalMap，在map里以threadlocal为key,set的值为value
//        同时作为key的值为弱引用(只能活到下一次gc之前)，发生gc会被释放，但是value是强引用，到后面会oom
//         threadLocal的作用是产生一个作用域，使存在map里的值可以在一个线程共享，由于会oom所以在线程结束之后remove掉
//        给threadlocal设定final，static，让整个线程只能获取值，不可更改
        return THREAD_LOCAL.get();
    }
//完成之后结束线程，这里我们用的是Tomcat线程池，所以显示的删除局部变量的值是必须的
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        THREAD_LOCAL.remove();
    }
}
