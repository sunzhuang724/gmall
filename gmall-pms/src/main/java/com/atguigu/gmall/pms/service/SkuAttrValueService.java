package com.atguigu.gmall.pms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;

import java.util.List;

/**
 * sku销售属性&值
 *
 * @author sun
 * @email 1500622671@qq.com
 * @date 2020-10-27 20:18:20
 */
public interface SkuAttrValueService extends IService<SkuAttrValueEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
//querySearchSkuAttrValueBySkuIdAndCid

    List<SkuAttrValueEntity> querySearchSkuAttrValueBySkuIdAndCid(Long skuId, Long cid);
}

