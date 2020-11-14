package com.atguigu.gmall.index.controller;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.index.feign.GmallPmsClient;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class IndexController {
    @Autowired
    private IndexService indexService;
    @Autowired
    private GmallPmsClient pmsClient;
    @GetMapping({"/index","/"})
    public String toIndex(Model model){
//        一级分类
        List<CategoryEntity> cates = this.indexService.queryLel1Categories();
//        TODO 广告

        model.addAttribute("categories",cates);
        return "index";
    }
    @GetMapping("/index/cates/{pid}")
    @ResponseBody
    public ResponseVo<List<CategoryEntity>> queryLvl2CategoriesWithSubsByPid(@PathVariable("pid")Long pid){
        List<CategoryEntity> categoryEntities= this.indexService.queryLvl2CatesWithSubsByPid(pid);

        System.out.println("=======3.categoryEntities==========="+categoryEntities);
        return ResponseVo.ok(categoryEntities);
    }


}
