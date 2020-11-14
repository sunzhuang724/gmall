package com.gmall.gmall.item.vo;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.entity.SkuImagesEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class ItemVo {
//    三级分类
    private List<CategoryEntity> categories;

//    品牌
    private Long brnadId;
    private String brandName;
//    spu
    private Long spuId;
    private String spuName;

//    sku
    private Long skuId;
    private String title;
    private String subTitle;
    private BigDecimal price;
    private Integer weight;
    private String defaultImage;

//    sku图片
    private List<SkuImagesEntity> imagies;

//    营销信息
    private List<ItemSaleVo> sales;
//    是否有货
    private Boolean store = false;

    private List<SaleAttrValueVo> saleAttrs;

    private Map<Long,Object> saleAttr;

    private String skusJson;

    // spu的海报信息
    private List<String> spuImages;

    // 规格参数组及组下的规格参数(带值)
    private List<GroupVo> groups;



}
