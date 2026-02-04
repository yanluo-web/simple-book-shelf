package com.example.bookshelf.controller;

import com.example.bookshelf.entity.Book;
import com.example.bookshelf.entity.Shelf;
import com.example.bookshelf.service.ShelfService;
import com.example.bookshelf.util.SecurityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
     * 书架详情接口（展示书架下所有书籍，原有功能）
     */
    @GetMapping("/shelfList/{shelfId}")
    public String shelfDetail(@PathVariable Long shelfId, Model model) {
        // 1. 查询当前书架信息
        Shelf shelf = shelfService.getShelfById(shelfId);
        // 2. 查询书架书籍列表
        List<Book> bookList = shelfService.getBookListByShelfId(shelfId, SecurityUtil.getCurrentUserId());

        // 3. 传递到前端
        model.addAttribute("shelf", shelf); // 传递整个书架对象
        model.addAttribute("bookList", bookList);
        return "book/shelf-detail";
    }

    /**
     * 新增：书架管理列表页（管理员查看所有书架）
     */
    @GetMapping("/shelfManage")
    public String shelfManage(Model model) {
        List<Shelf> shelfList = shelfService.getAllShelves();
        model.addAttribute("shelfList", shelfList);
        return "shelf/shelfManage"; // 对应模板：templates/shelf/shelfManage.html
    }

    /**
     * 新增：新增书架页面
     */
    @GetMapping("/addPage")
    public String addShelfPage() {
        return "shelf/add"; // 对应模板：templates/shelf/add.html
    }

    /**
     * 新增：新增书架提交接口
     */
    @PostMapping("/add")
    public String addShelf(@RequestParam String shelfName,
                           @RequestParam(required = false) String description,
                           RedirectAttributes redirectAttributes) {
        try {
            shelfService.addShelf(shelfName, description, SecurityUtil.getCurrentUserId());
            redirectAttributes.addFlashAttribute("successMsg", "书架新增成功！");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "书架新增失败：" + e.getMessage());
        }
        return "redirect:/shelf/shelfManage";
    }

    /**
     * 新增：修改书架页面
     */
    @GetMapping("/editPage/{shelfId}")
    public String editShelfPage(@PathVariable Long shelfId, Model model) {
        Shelf shelf = shelfService.getShelfById(shelfId);
        model.addAttribute("shelf", shelf);
        return "shelf/edit"; // 对应模板：templates/shelf/edit.html
    }

    /**
     * 新增：修改书架提交接口
     */
    @PostMapping("/edit")
    public String editShelf(@RequestParam Long shelfId,
                            @RequestParam String shelfName,
                            @RequestParam(required = false) String description,
                            RedirectAttributes redirectAttributes) {
        try {
            shelfService.updateShelf(shelfId, shelfName, description);
            redirectAttributes.addFlashAttribute("successMsg", "书架修改成功！");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "书架修改失败：" + e.getMessage());
        }
        return "redirect:/shelf/shelfManage";
    }

    /**
     * 新增：删除书架接口
     */
    @GetMapping("/delete/{shelfId}")
    public String deleteShelf(@PathVariable Long shelfId, RedirectAttributes redirectAttributes) {
        try {
            shelfService.deleteShelf(shelfId);
            redirectAttributes.addFlashAttribute("successMsg", "书架删除成功！");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "书架删除失败：" + e.getMessage());
        }
        return "redirect:/shelf/shelfManage";
    }
}