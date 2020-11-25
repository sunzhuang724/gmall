package com.atguigu.gmall.pms.api;

import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface GmallPmsApi {


    @GetMapping("pms/sku/{id}")
    public ResponseVo<SkuEntity> querySkuById(@PathVariable("id") Long id);
    @PostMapping("pms/spu/json")
    public ResponseVo<List<SpuEntity>> querySpuByPageJson(@RequestBody PageParamVo paramVo);
    @GetMapping("pms/sku/spu/{spuId}")
    public ResponseVo<List<SkuEntity>> querySkusBySpuId(@PathVariable("spuId")Long spuId);

    @GetMapping("pms/brand/{id}")
    public ResponseVo<BrandEntity> queryBrandById(@PathVariable("id") Long id);

    @GetMapping("pms/category/{id}")
    public ResponseVo<CategoryEntity> queryCategoryById(@PathVariable("id") Long id);

    @GetMapping("pms/skuattrvalue/search/attr/{skuId}")
    public ResponseVo<List<SkuAttrValueEntity>> querySearchSkuAttrValueBySkuIdAndCid(
            @PathVariable("skuId")Long skuId,
            @RequestParam("cid")Long cid
    );
    @GetMapping("pms/spuattrvalue/search/attr/{spuId}")
    public ResponseVo<List<SpuAttrValueEntity>> querySearchSpuAttrValueBySpuIdAndCid(
            @PathVariable("spuId")Long spuId,
            @RequestParam("cid")Long cid
    );

    @GetMapping("pms/spu/{id}")
    public ResponseVo<SpuEntity> querySpuById(@PathVariable("id") Long id);

    @GetMapping("pms/category/parent/{parentId}")
    public ResponseVo<List<CategoryEntity>> queryCategoryByPid(@PathVariable("parentId") Long pid);

    @GetMapping("pms/category/parent/sub/{parentId}")
    public ResponseVo<List<CategoryEntity>> queryLvl2CatesWithSubsByPid(@PathVariable("parentId")Long pid);

}