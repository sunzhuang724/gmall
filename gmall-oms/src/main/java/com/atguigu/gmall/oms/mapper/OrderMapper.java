package com.atguigu.gmall.oms.mapper;

import com.atguigu.gmall.oms.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author sun
 * @email 1500622671@qq.com
 * @date 2020-10-27 20:48:21
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderEntity> {
	
}
