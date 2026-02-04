package com.example.bookshelf.controller;

import com.example.bookshelf.entity.Book;
import com.example.bookshelf.service.BookChapterService;
import com.example.bookshelf.service.BookService;
import com.example.bookshelf.service.ReadRecordService;
import com.example.bookshelf.util.FileUtil;
import com.example.bookshelf.util.SecurityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import java.io.File;

/**
 * 图书管理控制器
 */
@Controller
@RequestMapping("/book")
public class BookController {

    @Resource
    private BookService bookService;
    @Resource
    private FileUtil fileUtil;
    @Resource
    private BookChapterService bookChapterService;
    @Resource
    private ReadRecordService readRecordService;

    /**
     * 书籍导入接口
     */
    @PostMapping("/import")
    public String importBook(@RequestParam("bookFile") MultipartFile bookFile,
                             @RequestParam("bookName") String bookName,
                             @RequestParam(required = false) String author,
                             @RequestParam(required = false) Long shelfId,
                             RedirectAttributes redirectAttributes) {
        try {
            // 1. 上传文件
            String filePath = fileUtil.uploadBookFile(bookFile);

            // 2. 解析书籍信息
            String originalFileName = bookFile.getOriginalFilename();
            assert originalFileName != null;
            String format = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toUpperCase();

            // 3. 入库
            bookService.saveBook(
                    bookName,
                    author,
                    "",
                    filePath,
                    format,
                    shelfId == null ? 1L : shelfId,
                    getCurrentUserId()
            );

            // 4. 传递成功提示（Flash 属性）
            redirectAttributes.addFlashAttribute("msg", "导入成功");
            return "redirect:/book/importPage";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "导入失败：" + e.getMessage());
            return "redirect:/book/importPage";
        }
    }

    /**
     * 书籍导入页接口
     */
    @GetMapping("/importPage")
    public String importPage() {
        // 跳转至 book/import.html 页面
        return "book/import";
    }

    /**
     * 拆分书籍章节（仅支持 TXT 格式）
     */
    @PostMapping("/splitChapters/{bookId}")
    public String splitBookChapters(@PathVariable Long bookId,
                                    RedirectAttributes redirectAttributes) {
        try {
            // 1. 查询书籍信息
            Book book = bookService.getBookById(bookId);
            if (book == null) {
                redirectAttributes.addFlashAttribute("errorMsg", "书籍不存在，无法拆分章节");
                return "redirect:/shelf/shelfList/1"; // 修正拼写错误：shelfLsit -> shelfList
            }

            // 2. 验证书籍格式
            if (!"TXT".equals(book.getFormat())) {
                redirectAttributes.addFlashAttribute("errorMsg", "仅支持 TXT 格式书籍拆分章节");
                return "redirect:/shelf/shelfList/1";
            }

            // 3. 构建 TXT 文件路径
            String bookFilePath = book.getFilePath();
            File txtFile = new File(bookFilePath);

            // 4. 验证文件是否存在
            if (!txtFile.exists()) {
                redirectAttributes.addFlashAttribute("errorMsg", "TXT 文件不存在，无法拆分章节");
                return "redirect:/shelf/shelfList/1";
            }

            // 5. 调用章节拆分服务
            bookChapterService.splitTxtToChapters(book, txtFile);

            // 6. 拆分成功提示
            redirectAttributes.addFlashAttribute("successMsg", "章节拆分成功！可点击「立即阅读」查看");
        } catch (Exception e) {
            // 异常处理
            redirectAttributes.addFlashAttribute("errorMsg", "章节拆分失败：" + e.getMessage());
            e.printStackTrace();
        }

        // 重定向回书架页面
        return "redirect:/shelf/shelfList/1";
    }

    /**
     * 【新增】删除书籍接口
     */
    @GetMapping("/delete/{bookId}")
    public String deleteBook(@PathVariable Long bookId, RedirectAttributes redirectAttributes) {
        try {
            // 1. 查询书籍信息
            Book book = bookService.getBookById(bookId);
            if (book == null) {
                redirectAttributes.addFlashAttribute("errorMsg", "删除失败：书籍不存在");
                return "redirect:/shelf/shelfList/1";
            }

            // 2. 权限校验（可选但推荐：确保只能删除自己的书）
            Long currentUserId = getCurrentUserId();
            if (!currentUserId.equals(book.getUploadUserId())) {
                redirectAttributes.addFlashAttribute("errorMsg", "删除失败：你没有权限删除该书籍");
                return "redirect:/shelf/shelfList/1";
            }

            // 3. 删除服务器上的书籍文件
            String filePath = book.getFilePath();
            File bookFile = new File(filePath);
            if (bookFile.exists() && !bookFile.delete()) {
                // 如果文件删除失败，可以选择记录日志或抛出异常，这里选择提示但继续删除数据库记录
                redirectAttributes.addFlashAttribute("warnMsg", "书籍文件删除失败，但数据库记录已清除");
            }

            // 4. 删除数据库中的书籍关联数据（章节、阅读记录）和书籍本身
            bookChapterService.deleteChaptersByBookId(bookId); // 先删章节
            readRecordService.deleteReadRecordsByBookId(bookId); // 再删阅读记录
            bookService.deleteBookById(bookId); // 最后删书籍

            // 5. 成功提示
            redirectAttributes.addFlashAttribute("successMsg", "书籍《" + book.getBookName() + "》删除成功！");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMsg", "删除失败：" + e.getMessage());
        }

        // 6. 重定向回书架页面
        return "redirect:/shelf/shelfList/1";
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        return SecurityUtil.getCurrentUserId();
    }
}