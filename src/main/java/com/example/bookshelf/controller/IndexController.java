package com.example.bookshelf.controller;

import com.example.bookshelf.entity.Shelf;
import com.example.bookshelf.service.ShelfService;
import com.example.bookshelf.util.SecurityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class IndexController {

    @Resource
    private ShelfService shelfService;

    /**
     * 跳转登录页
     */
    @GetMapping("/login")
    public String toLogin() {
        return "login";
    }

    /**
     * 首页接口（展示当前用户的所有书架）
     */
    @GetMapping("/index")
    public String index(Model model) {
        // 获取当前登录用户ID
        Long currentUserId = SecurityUtil.getCurrentUserId();
        // 查询当前用户的所有书架
        List<Shelf> shelfList = shelfService.getShelvesByUserId(currentUserId);

        model.addAttribute("shelfList", shelfList);
        return "index";
    }
}