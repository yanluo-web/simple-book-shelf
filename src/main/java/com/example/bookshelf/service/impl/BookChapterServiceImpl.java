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
     * 拆分 TXT 文件为章节并保存到数据库
     * @param book 所属书籍实体
     * @param txtFile 上传的 TXT 文件
     */
    @Transactional
    public void splitTxtToChapters(Book book, File txtFile) {
        try {
            // 1. 读取 TXT 文件内容（优先 UTF-8，若乱码可尝试 GBK）
            String txtContent = TxtReadUtil.readTxtFileWithCharsetAuto(txtFile);
            if (StringUtils.isBlank(txtContent)) {
                throw new RuntimeException("TXT 文件内容为空，无法拆分章节");
            }

            // 2. 正则匹配章节标题（支持：第X章、第XX章、Chapter X）
            String chapterRegex = "(第[一二三四五六七八九十百千万\\d]+章(?:\\s+.+)?)|(Chapter\\s+\\d+(?:\\s+.+)?)";
            Pattern pattern = Pattern.compile(chapterRegex);
            Matcher matcher = pattern.matcher(txtContent);

            // 3. 收集章节起始位置和标题
            List<ChapterPosition> chapterPositions = new ArrayList<>();
            while (matcher.find()) {
                String chapterTitle = matcher.group().trim();
                int startIndex = matcher.start();
                chapterPositions.add(new ChapterPosition(startIndex, chapterTitle));
            }

            // 4. 处理无匹配章节的情况（整本书作为一个章节）
            List<BookChapter> bookChapters = new ArrayList<>();
            if (chapterPositions.isEmpty()) {
                BookChapter chapter = new BookChapter();
                chapter.setChapterTitle(book.getBookName() + "（全本）");
                chapter.setChapterContent(txtContent);
                chapter.setChapterOrder(1);
                chapter.setBookId(book.getId());
                bookChapters.add(chapter);
            } else {
                // 5. 截取每个章节的内容并封装实体
                int chapterCount = chapterPositions.size();
                for (int i = 0; i < chapterCount; i++) {
                    ChapterPosition current = chapterPositions.get(i);
                    int start = current.getStartIndex();
                    int end = (i == chapterCount - 1) ? txtContent.length() : chapterPositions.get(i + 1).getStartIndex();

                    // 截取章节内容（去除前后空白，优化显示）
                    String chapterContent = txtContent.substring(start, end).trim();

                    // 封装章节实体
                    BookChapter chapter = new BookChapter();
                    chapter.setChapterTitle(current.getChapterTitle());
                    chapter.setChapterContent(chapterContent);
                    chapter.setChapterOrder(i + 1); // 章节序号从 1 开始
                    chapter.setBookId(book.getId());
                    bookChapters.add(chapter);
                }
            }

            // 6. 批量保存章节到数据库
            bookChapterMapper.saveAll(bookChapters);
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
}
