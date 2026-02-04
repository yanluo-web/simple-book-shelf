package com.example.bookshelf.controller;

import com.example.bookshelf.entity.Book;
import com.example.bookshelf.entity.Shelf;
import com.example.bookshelf.service.BookChapterService;
import com.example.bookshelf.service.BookService;
import com.example.bookshelf.service.ReadRecordService;
import com.example.bookshelf.service.ShelfService;
import com.example.bookshelf.util.FileUtil;
import com.example.bookshelf.util.SecurityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

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
    @Resource
    private ShelfService shelfService;

    /**
     * 书籍导入接口（导入成功后重定向到目标书架，不变）
     */
    @PostMapping("/import")
    public String importBook(@RequestParam("bookFile") MultipartFile bookFile,
                             @RequestParam("bookName") String bookName,
                             @RequestParam(required = false) String author,
                             @RequestParam(required = false) Long shelfId,
                             RedirectAttributes redirectAttributes) {
        try {
            String filePath = fileUtil.uploadBookFile(bookFile);
            String originalFileName = bookFile.getOriginalFilename();
            assert originalFileName != null;
            String format = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toUpperCase();
            Long targetShelfId = shelfId == null ? 1L : shelfId;

            bookService.saveBook(bookName, author, "", filePath, format, targetShelfId, getCurrentUserId());
            redirectAttributes.addFlashAttribute("successMsg", "导入成功");
            return "redirect:/shelf/shelfList/" + targetShelfId; // 导入后重定向到目标书架
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMsg", "导入失败：" + e.getMessage());
            return "redirect:/book/importPage";
        }
    }

    /**
     * 书籍导入页接口
     */
    @GetMapping("/importPage")
    public String importPage(Model model) {
        // 1. 获取当前登录用户ID
        Long currentUserId = SecurityUtil.getCurrentUserId();

        // 2. 查询该用户的所有书架
        List<Shelf> userShelves = shelfService.getShelvesByUserId(currentUserId);

        // 3. 将书架列表放入Model，供前端下拉框使用
        model.addAttribute("shelfList", userShelves);

        return "book/import";
    }

    /**
     * 拆分书籍章节（接收前端传的shelfId，重定向回当前书架）
     */
    @PostMapping("/splitChapters/{bookId}")
    public String splitBookChapters(@PathVariable Long bookId,
                                    @RequestParam(required = false) Long shelfId, // 接收前端传的书架ID
                                    RedirectAttributes redirectAttributes) {
        try {
            Book book = bookService.getBookById(bookId);
            if (book == null) {
                redirectAttributes.addFlashAttribute("errorMsg", "书籍不存在，无法拆分章节");
                return "redirect:/shelf/shelfList/" + getFinalShelfId(shelfId, null);
            }

            if (!"TXT".equals(book.getFormat())) {
                redirectAttributes.addFlashAttribute("errorMsg", "仅支持 TXT 格式书籍拆分章节");
                return "redirect:/shelf/shelfList/" + getFinalShelfId(shelfId, book.getShelfId());
            }

            File txtFile = new File(book.getFilePath());
            if (!txtFile.exists()) {
                redirectAttributes.addFlashAttribute("errorMsg", "TXT 文件不存在，无法拆分章节");
                return "redirect:/shelf/shelfList/" + getFinalShelfId(shelfId, book.getShelfId());
            }

            bookChapterService.splitTxtToChapters(book, txtFile);
            redirectAttributes.addFlashAttribute("successMsg", "章节拆分成功！可点击「立即阅读」查看");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "章节拆分失败：" + e.getMessage());
            e.printStackTrace();
        }

        // 重定向：优先用前端传的shelfId，兜底到书籍所属书架，再兜底到1
        return "redirect:/shelf/shelfList/" + getFinalShelfId(shelfId, null);
    }

    /**
     * 删除书籍接口（接收前端传的shelfId，重定向回当前书架）
     */
    @GetMapping("/delete/{bookId}")
    public String deleteBook(@PathVariable Long bookId,
                             @RequestParam(required = false) Long shelfId, // 接收前端传的书架ID
                             RedirectAttributes redirectAttributes) {
        Book book = null;
        try {
            book = bookService.getBookById(bookId);
            if (book == null) {
                redirectAttributes.addFlashAttribute("errorMsg", "删除失败：书籍不存在");
                return "redirect:/shelf/shelfList/" + getFinalShelfId(shelfId, null);
            }

            // 权限校验
            Long currentUserId = getCurrentUserId();
            if (!currentUserId.equals(book.getUploadUserId())) {
                redirectAttributes.addFlashAttribute("errorMsg", "删除失败：你没有权限删除该书籍");
                return "redirect:/shelf/shelfList/" + getFinalShelfId(shelfId, book.getShelfId());
            }

            // 删除文件
            File bookFile = new File(book.getFilePath());
            if (bookFile.exists() && !bookFile.delete()) {
                redirectAttributes.addFlashAttribute("warnMsg", "书籍文件删除失败，但数据库记录已清除");
            }

            // 删除关联数据
            bookChapterService.deleteChaptersByBookId(bookId);
            readRecordService.deleteReadRecordsByBookId(bookId);
            bookService.deleteBookById(bookId);

            redirectAttributes.addFlashAttribute("successMsg", "书籍《" + book.getBookName() + "》删除成功！");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMsg", "删除失败：" + e.getMessage());
        }

        // 重定向：优先用前端传的shelfId，兜底到书籍所属书架，再兜底到1
        return "redirect:/shelf/shelfList/" + getFinalShelfId(shelfId, book != null ? book.getShelfId() : null);
    }

    /**
     * 【工具方法】获取最终重定向的书架ID（多层兜底）
     * 优先级：前端传的shelfId → 书籍所属书架ID → 默认书架1
     */
    private Long getFinalShelfId(Long frontShelfId, Long bookShelfId) {
        if (frontShelfId != null) {
            return frontShelfId; // 第一优先级：前端传递的当前书架ID
        }
        if (bookShelfId != null) {
            return bookShelfId; // 第二优先级：书籍所属书架ID
        }
        return 1L; // 第三优先级：默认书架
    }

    /**
     * 获取当前登录用户ID（不变）
     */
    private Long getCurrentUserId() {
        return SecurityUtil.getCurrentUserId();
    }
}