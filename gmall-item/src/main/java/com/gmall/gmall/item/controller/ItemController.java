package com.gmall.gmall.item.controller;

import com.atguigu.gmall.common.bean.ResponseVo;


import com.gmall.gmall.item.service.ItemService;
import com.gmall.gmall.item.vo.ItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping("{skuId}.html")
//    @ResponseBody
    public String loadData(@PathVariable("skuId")Long skuId, Model model){
        ItemVo itemVo = this.itemService.loadData(skuId);
        model.addAttribute("itemVo", itemVo);
        System.out.println("=============="+itemVo);
        return "item";
    }
}
