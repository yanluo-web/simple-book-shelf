package com.example.bookshelf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 娱乐模块控制器
 */
@Controller
@RequestMapping("/entertainment")
public class EntertainmentController {

    /**
     * 娱乐模块主页面（居中菜单）
     */
    @GetMapping("/index")
    public String entertainmentIndex() {
        return "entertainment/index";
    }

    /**
     * 海克斯卡牌空页面
     */
    @GetMapping("/hexCard")
    public String hexCard() {
        return "entertainment/hexCard";
    }
}