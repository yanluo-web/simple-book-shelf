package com.example.bookshelf.service.impl;

import com.example.bookshelf.entity.Book;
import com.example.bookshelf.entity.BookChapter;
import com.example.bookshelf.mapper.BookChapterMapper;
import com.example.bookshelf.service.BookChapterService;
import com.example.bookshelf.util.TxtReadUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 书籍章节业务服务
 */
@Service
@RequiredArgsConstructor
public class BookChapterServiceImpl implements BookChapterService {
    private final BookChapterMapper bookChapterMapper;

    /**
     * 拆分 TXT 文件为章节并保存到数据库（新增序、节的检索）
     * @param book 所属书籍实体
     * @param txtFile 上传的 TXT 文件
     */
    @Transactional
    public void splitTxtToChapters(Book book, File txtFile) {
        try {
            // 1. 读取 TXT 文件内容（优先 UTF-8，若乱码可尝试 GBK）
            String txtContent = TxtReadUtil.readTxtFileWithCharsetAuto(txtFile);
            if (StringUtils.isEmpty(txtContent)) {
                throw new RuntimeException("TXT 文件内容为空，无法拆分章节");
            }

            // 2. 正则匹配章节标题（新增：序：、第X节；保留：第X章、Chapter X）
            // 匹配规则：
            // - 序：必须带冒号（序：、序言：、引子：）
            // - 节：第X节、第XX节（支持中文数字/阿拉伯数字）
            // - 章：原有规则不变
            String chapterRegex =
                    "(序(?:言|子)?[:：])|" +  // 仅匹配“序：”“序言：”“引子：”（必须带冒号）
                            "(第[一二三四五六七八九十百千万\\d]+节(?:\\s+.+)?)|" +  // 匹配“第1节”“第三节 标题”等
                            "(第[一二三四五六七八九十百千万\\d]+章(?:\\s+.+)?)|" +  // 原有章匹配
                            "(Chapter\\s+\\d+(?:\\s+.+)?)";
            Pattern pattern = Pattern.compile(chapterRegex);
            Matcher matcher = pattern.matcher(txtContent);

            // 3. 收集章节起始位置和标题（兼容序、节、章）
            List<ChapterPosition> chapterPositions = new ArrayList<>();
            while (matcher.find()) {
                String chapterTitle = matcher.group().trim();
                int startIndex = matcher.start();
                // 优化标题显示（比如去掉多余的冒号）
                chapterTitle = chapterTitle.replaceAll("[:：]$", "").trim();
                chapterPositions.add(new ChapterPosition(startIndex, chapterTitle));
            }

            // 4. 处理无匹配章节的情况（整本书作为一个章节）
            List<BookChapter> bookChapters = new ArrayList<>();
            Long bookId = book.getId(); // 提取书籍ID，避免重复获取
            if (chapterPositions.isEmpty()) {
                // 检查唯一章节（序号1）是否存在
                Integer existCount = bookChapterMapper.findCountByBookIdByChapterOrder(bookId, 1L);
                if (existCount == null || existCount == 0) {
                    BookChapter chapter = new BookChapter();
                    chapter.setChapterTitle(book.getBookName() + "（全本）");
                    chapter.setChapterContent(txtContent);
                    chapter.setChapterOrder(1);
                    chapter.setBookId(bookId);
                    bookChapters.add(chapter);
                }
            } else {
                // 5. 截取每个章节的内容并封装实体（增加存在性校验）
                int chapterCount = chapterPositions.size();
                for (int i = 0; i < chapterCount; i++) {
                    ChapterPosition current = chapterPositions.get(i);
                    int start = current.getStartIndex();
                    // 最后一个章节取到文本末尾，否则取下个章节的起始位置
                    int end = (i == chapterCount - 1) ? txtContent.length() : chapterPositions.get(i + 1).getStartIndex();

                    // 章节序号（从1开始，序会作为第1节/章，后续依次递增）
                    Long chapterOrder = (long) (i + 1);

                    // 第一步：先校验该章节是否已存在
                    Integer existCount = bookChapterMapper.findCountByBookIdByChapterOrder(bookId, chapterOrder);
                    if (existCount != null && existCount > 0) {
                        continue; // 存在则跳过当前章节
                    }

                    // 第二步：截取章节内容（去除前后空白，优化显示）
                    String chapterContent = txtContent.substring(start, end).trim();

                    // 第三步：封装章节实体
                    BookChapter chapter = new BookChapter();
                    chapter.setChapterTitle(current.getChapterTitle());
                    chapter.setChapterContent(chapterContent);
                    chapter.setChapterOrder(Math.toIntExact(chapterOrder));
                    chapter.setBookId(bookId);
                    bookChapters.add(chapter);
                }
            }

            // 6. 批量保存章节到数据库（仅保存不存在的章节）
            if (!CollectionUtils.isEmpty(bookChapters)) {
                bookChapterMapper.saveAll(bookChapters);
            }
        } catch (Exception e) {
            throw new RuntimeException("TXT 章节拆分失败：" + e.getMessage(), e);
        }
    }


    /**
     * 辅助类：存储章节起始位置和标题
     */
    private static class ChapterPosition {
        private final int startIndex;
        private final String chapterTitle;

        public ChapterPosition(int startIndex, String chapterTitle) {
            this.startIndex = startIndex;
            this.chapterTitle = chapterTitle;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public String getChapterTitle() {
            return chapterTitle;
        }
    }

    /**
     * 根据书籍查询所有章节
     */
    public List<BookChapter> getChaptersByBook(Book book) {
        return bookChapterMapper.findByBookIdOrderByChapterOrderAsc(book.getId());
    }

    /**
     * 根据书籍和章节序号查询章节
     */
    public BookChapter getChapterByBookAndOrder(Book book, Integer chapterOrder) {
        return bookChapterMapper.findByBookIdAndChapterOrder(book.getId(), chapterOrder);
    }

    /**
     * 新增：根据章节ID查询章节（封装 JpaRepository 的 findById，解决报红）
     * @param chapterId 章节主键ID
     * @return 章节实体
     */
    public BookChapter getById(Long chapterId) {
        // 调用 JpaRepository 自带的 findById，若不存在则抛出异常，方便前端排查
        return bookChapterMapper.findById(chapterId);
    }

    /**
     * 书籍删除
     * @param bookId
     */
    public void deleteChaptersByBookId(Long bookId) {
        bookChapterMapper.deleteByBookId(bookId);
    }
}
