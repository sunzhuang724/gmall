package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.interceptor.LoginInterceptor;
import com.atguigu.gmall.cart.pojo.Cart;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.bean.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CartController {
    @Autowired
    private LoginInterceptor loginInterceptor;
    @Autowired
    private CartService cartService;
//    新增购物车
    @GetMapping
    public String addCart(Cart cart){
        cartService.addCart(cart);
        return "redirect:http://cart.gmall.com/addCart.html?skuId="+cart.getSkuId();
    }
    //    查询购物车
    @GetMapping("addCart.html")
    public String queryCart(@RequestParam("skuId")Long skuId, Model model){
        Cart cart = this.cartService.queryCart(skuId);
        model.addAttribute("cart",cart);
        return "addCart";
    }
    @PostMapping("updateNum")
    @ResponseBody
    public ResponseVo updateNum(@RequestBody Cart cart){
        this.cartService.updateNum(cart);
        return ResponseVo.ok();
    }
    @GetMapping("cart.html")
    public String queryCarts(Model model){
        List<Cart> carts = this.cartService.queryCarts();
        model.addAttribute("carts", carts);
        return "cart";
    }
    @PostMapping("deleteCart")
    @ResponseBody
    public ResponseVo deleteCart(@RequestParam("skuId")Long skuId){
        this.cartService.deleteCart(skuId);
        return ResponseVo.ok();
    }

    @GetMapping("test")
    public String test(HttpServletRequest request){
        System.out.println("这是一个handler方法"+loginInterceptor.getUserInfo());
        return "hello intercepter";
    }


}
