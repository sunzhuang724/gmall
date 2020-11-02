package com.atguigu.gmall.pms.feign;



import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.sms.api.GmallSmsApi;
import com.atguigu.gmall.sms.vo.SkuSaleVo;
import org.springframework.cloud.openfeign.FeignClient;


@FeignClient("sms-service")
public interface GmallSmsClient extends GmallSmsApi {

}
