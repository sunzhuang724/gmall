package com.atguigu.gmall.scheduled.jobhandler;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class myJobhandler {
//    注解中是一个唯一标志
    @XxlJob("myJObhandler")
    public ReturnT<String> handler(String param){
        System.out.println("this is a response from executor");
        log.info("接收到了调度中心的参数"+param);
        return ReturnT.SUCCESS;
    }
}
