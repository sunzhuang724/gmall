package com.atguigu.gmall.search.pojo;

import lombok.Data;

import java.util.List;

@Data
public class SearchParamVo {
//搜索功能数据封装
//    搜索关键字
    private String keyword;
//    品牌过滤
    private List<Long> brandId;
//    分类的过滤条件
    private List<Long> categoryId;

//    规格参数的过滤
    private List<String> props;

//    排序字段
    private Integer sort;
//    价格区间的过滤条件
    private Double priceTo;
    private Double priceFrom;

    private Boolean store;
//分页，默认当前为以1
    private Integer pageNum = 1;
    private final  Integer pageSize = 20;
}
