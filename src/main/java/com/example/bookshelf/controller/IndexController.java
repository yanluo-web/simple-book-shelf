package com.example.bookshelf.controller;

import com.example.bookshelf.service.ShelfService;
import com.example.bookshelf.util.SecurityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;

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
     * 首页接口（传递默认书架书籍数量）
     * @param model
     * @return
     */
    @GetMapping("/index")
    public String index(Model model) {
        // 默认书架 ID=1（对应 data.sql 中插入的默认书架）
        Long defaultShelfId = 1L;
        // 查询默认书架书籍数量
        Integer bookCount = shelfService.getBookCountByShelfId(defaultShelfId, SecurityUtil.getCurrentUserId());
        // 传递到前端页面
        model.addAttribute("defaultShelfId", defaultShelfId);
        model.addAttribute("bookCount", bookCount);
        return "index";
    }
}