package com.example.bookshelf.controller;

import com.example.bookshelf.entity.Book;
import com.example.bookshelf.service.ShelfService;
import com.example.bookshelf.util.SecurityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.List;

/**
 * 书架管理控制器
 */
@Controller
@RequestMapping("/shelf")
public class ShelfController {
    @Resource
    private ShelfService shelfService;
    /**
     * 书架详情接口（展示书架下所有书籍）
     */
    @GetMapping("/shelfLsit/{shelfId}")
    public String shelfDetail(@PathVariable Long shelfId, Model model) {
        // 查询书架书籍列表
        List<Book> bookList = shelfService.getBookListByShelfId(shelfId, SecurityUtil.getCurrentUserId());
        // 传递到前端
        model.addAttribute("shelfId", shelfId);
        model.addAttribute("bookList", bookList);
        return "book/shelf-detail";
    }
}
