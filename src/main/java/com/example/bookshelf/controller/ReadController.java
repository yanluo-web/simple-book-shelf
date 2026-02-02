package com.example.bookshelf.controller;

import com.example.bookshelf.entity.Book;
import com.example.bookshelf.entity.BookChapter;
import com.example.bookshelf.entity.ReadRecord;
import com.example.bookshelf.entity.User;
import com.example.bookshelf.service.BookChapterService;
import com.example.bookshelf.service.BookService;
import com.example.bookshelf.service.ReadRecordService;
import com.example.bookshelf.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 阅读与章节管理控制器
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/read")
public class ReadController {
    private final BookService bookService;
    private final BookChapterService bookChapterService;
    private final ReadRecordService readRecordService;

    /**
     * 进入书籍阅读页面（优先恢复上次阅读记录，强化空值兜底）
     * @param bookId 书籍ID
     * @param model 页面数据模型
     */
    @GetMapping("/onRead/{bookId}")
    public String enterReadPage(@PathVariable Long bookId,
                                Model model) {
        // 1. 临时用户（后续替换为 Spring Security 真实用户）
        User user = new User();
        user.setId(SecurityUtil.getCurrentUserId());

        // 2. 查询书籍信息并校验
        Book book = bookService.getBookById(bookId);
        if (book == null) {
            model.addAttribute("error", "书籍不存在");
            return "error";
        }

        // 3. 查询书籍所有章节（重点：兜底空列表，避免 null）
        List<BookChapter> chapters = new ArrayList<>(); // 初始化空列表，避免后续 NPE
        try {
            List<BookChapter> tempChapters = bookChapterService.getChaptersByBook(book);
            if (tempChapters != null && !tempChapters.isEmpty()) {
                chapters = tempChapters;
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "章节数据加载失败");
            return "error";
        }

        // 4. 校验章节列表，无章节直接返回错误
        if (chapters.isEmpty()) {
            model.addAttribute("error", "该书籍暂无章节，请先拆分TXT");
            model.addAttribute("book", book);
            return "error";
        }

        // 5. 处理阅读记录，确保 targetChapter 非 null（重点兜底，解决 targetChapter 报红）
        BookChapter targetChapter = null;
        Integer readPosition = 0;
        try {
            Optional<ReadRecord> optionalRecord = readRecordService.getReadRecordByUserAndBook(user, book);
            if (optionalRecord.isPresent()) {
                ReadRecord record = optionalRecord.get();
                targetChapter = bookChapterService.getById(record.getChapterId());
                // 额外校验：阅读记录中的章节是否存在于当前章节列表（修复报红）
                boolean chapterExists = false;
                // 先判断 targetChapter 非 null，再调用 getId()，避免报红和 NPE
                if (targetChapter != null) {
                    BookChapter finalTargetChapter = targetChapter;
                    chapterExists = chapters.stream().anyMatch(c -> c.getId().equals(finalTargetChapter.getId()));
                }
                // 记录失效、targetChapter 为 null，都默认取第一章
                if (!chapterExists) {
                    targetChapter = chapters.get(0);
                }
                readPosition = Optional.ofNullable(record.getReadPosition()).orElse(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 最终兜底：无论何种情况，确保 targetChapter 取第一章（核心修复）
        if (targetChapter == null) {
            targetChapter = chapters.get(0);
        }

        // 6. 封装数据到页面（此时 currentChapter 一定非 null）
        model.addAttribute("book", book);
        model.addAttribute("chapters", chapters);
        model.addAttribute("currentChapter", targetChapter);
        model.addAttribute("readPosition", readPosition);

        // 注意：模板路径是 "book/read"，对应 templates/book/read.html（和日志中的模板路径一致）
        return "book/read";
    }

    /**
     * 切换章节阅读（保持原有逻辑，强化章节校验）
     */
    @GetMapping("/switchChapter")
    public String switchChapter(@RequestParam Long bookId,
                                @RequestParam Integer chapterOrder,
                                Model model) {
        User user = new User();
        user.setId(SecurityUtil.getCurrentUserId());

        Book book = bookService.getBookById(bookId);
        if (book == null) {
            model.addAttribute("error", "书籍不存在");
            return "error";
        }

        BookChapter chapter = null;
        try {
            chapter = bookChapterService.getChapterByBookAndOrder(book, chapterOrder);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (chapter == null) {
            model.addAttribute("error", "章节不存在");
            model.addAttribute("book", book);
            return "error";
        }
        System.out.println(chapter);

        try {
            readRecordService.saveOrUpdateReadRecord(user, book, chapter, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/read/" + bookId;
    }

    /**
     * 更新阅读位置（AJAX调用，强化数据校验）
     */
    @PostMapping("/updatePosition")
    @ResponseBody // 关键：添加该注解，告知Spring返回JSON数据，不查找Thymeleaf模板
    public Map<String, Object> updateReadPosition(@RequestParam Long bookId,
                                                  @RequestParam Long chapterId,
                                                  @RequestParam Integer readPosition,
                                                  Model model) {
        // 1. 初始化返回结果（给前端明确的响应反馈）
        Map<String, Object> result = new HashMap<>();

        // 2. 强化数据校验（边界值、非空校验）
        if (bookId == null || bookId <= 0 ||
                chapterId == null || chapterId <= 0 ||
                readPosition == null || readPosition < 0) {
            result.put("success", false);
            result.put("msg", "参数无效：书籍ID、章节ID不能为空且大于0，阅读位置不能为负数");
            return result;
        }

        // 3. 当前登录用户
        User user = new User();
        user.setId(SecurityUtil.getCurrentUserId());

        // 4. 查询书籍和章节（优化异常处理，避免空指针）
        Book book = bookService.getBookById(bookId);
        BookChapter chapter = bookChapterService.getById(chapterId); // 直接调用，异常统一捕获

        // 5. 校验书籍和章节是否存在
        if (book == null || chapter == null) {
            result.put("success", false);
            result.put("msg", "书籍或章节不存在，无法更新阅读位置");
            return result;
        }

        try {
            // 6. 执行阅读位置更新
            readRecordService.saveOrUpdateReadRecord(user, book, chapter, readPosition);
            result.put("success", true);
            result.put("msg", "阅读位置更新成功");
        } catch (Exception e) {
            // 7. 异常捕获并反馈给前端
            e.printStackTrace();
            result.put("success", false);
            result.put("msg", "更新阅读位置失败：" + e.getMessage());
        }

        // 8. 返回JSON结果给前端AJAX
        return result;
    }
}