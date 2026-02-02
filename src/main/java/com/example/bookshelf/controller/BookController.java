package com.example.bookshelf.controller;

import com.example.bookshelf.entity.Book;
import com.example.bookshelf.service.BookChapterService;
import com.example.bookshelf.service.BookService;
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
//            String bookName = originalFileName.substring(0, originalFileName.lastIndexOf("."));
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
            return "redirect:/book/importPage"; // 跳转至书籍导入页（需保持完整路径，避免 404）
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "导入失败：" + e.getMessage());
            return "redirect:/book/importPage"; // 跳转回导入页
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
     * 新增：拆分书籍章节（仅支持 TXT 格式）
     */
    @PostMapping("/splitChapters/{bookId}")
    public String splitBookChapters(@PathVariable Long bookId,
                                    RedirectAttributes redirectAttributes) {
        try {
            // 1. 查询书籍信息
            Book book = bookService.getBookById(bookId);
            if (book == null) {
                redirectAttributes.addFlashAttribute("errorMsg", "书籍不存在，无法拆分章节");
                return "redirect:/shelf/shelfLsit/1"; // 跳转至默认书架（保持完整路径，避免 404）
            }

            // 2. 验证书籍格式（双重校验，防止前端绕过禁用）
            if (!"TXT".equals(book.getFormat())) {
                redirectAttributes.addFlashAttribute("errorMsg", "仅支持 TXT 格式书籍拆分章节");
                return "redirect:/shelf/shelfLsit/1";
            }

            // 3. 构建 TXT 文件路径（需与你项目的书籍上传路径保持一致！）
            String bookFilePath = book.getFilePath();
            File txtFile = new File(bookFilePath);

            // 4. 验证文件是否存在
            if (!txtFile.exists()) {
                redirectAttributes.addFlashAttribute("errorMsg", "TXT 文件不存在，无法拆分章节");
                return "redirect:/shelf/shelfLsit/1";
            }

            // 5. 调用章节拆分服务，执行拆分
            bookChapterService.splitTxtToChapters(book, txtFile);

            // 6. 拆分成功，传递成功提示
            redirectAttributes.addFlashAttribute("successMsg", "章节拆分成功！共拆分出对应章节，可点击「立即阅读」查看");
        } catch (Exception e) {
            // 7. 异常处理，传递错误提示
            redirectAttributes.addFlashAttribute("errorMsg", "章节拆分失败：" + e.getMessage());
            e.printStackTrace();
        }

        // 8. 重定向回书架页面（保持原有书架上下文）
        return "redirect:/shelf/shelfLsit/1"; // 可调整为动态书架ID（如从参数获取）
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        return SecurityUtil.getCurrentUserId();
    }
}