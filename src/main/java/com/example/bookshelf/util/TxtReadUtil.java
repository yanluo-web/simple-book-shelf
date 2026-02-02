package com.example.bookshelf.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * TXT读取编码格式处理
 */
public class TxtReadUtil {

    /**
     * 优先UTF-8读取TXT文件，失败则尝试GBK
     * @param txtFile 待读取的TXT文件
     * @return 读取到的TXT文件内容
     * @throws RuntimeException 读取失败或文件内容为空时抛出异常
     */
    public static String readTxtFileWithCharsetAuto(File txtFile) {
        // 1. 优先使用 UTF-8 编码读取
        String txtContent = readFileByCharset(txtFile, StandardCharsets.UTF_8);

        // 2. 判断UTF-8读取结果是否有效（为空 或 疑似乱码，则尝试GBK）
        if (isContentInvalid(txtContent)) {
            txtContent = readFileByCharset(txtFile, Charset.forName("GBK"));
        }

        // 3. 最终判断内容是否为空（无论哪种编码，读取后为空则抛异常）
        if (StringUtils.isBlank(txtContent)) {
            throw new RuntimeException("TXT 文件内容为空，无法拆分章节");
        }

        return txtContent;
    }

    /**
     * 按指定编码读取文件内容
     * @param txtFile 待读取的文件
     * @param charset 编码格式
     * @return 读取到的文件内容
     */
    private static String readFileByCharset(File txtFile, Charset charset) {
        try {
            return FileUtils.readFileToString(txtFile, charset);
        } catch (Exception e) {
            throw new RuntimeException("使用 " + charset.displayName() + " 编码读取TXT文件失败", e);
        }
    }

    /**
     * 判断读取的内容是否无效（为空 或 包含大量乱码特征）
     * @param content 读取到的文件内容
     * @return true=无效，false=有效
     */
    private static boolean isContentInvalid(String content) {
        // 先判断是否为空
        if (StringUtils.isBlank(content)) {
            return true;
        }

        // 简单判断疑似乱码（UTF-8读取GBK中文时，常出现 � 这类替换字符）
        // 可根据实际场景调整乱码判断规则
        long garbageCharCount = content.chars()
                .filter(c -> c == '�') // 核心乱码特征字符
                .count();

        // 当乱码字符数量超过一定比例（这里设为5%，可调整），判定为乱码
        double garbageRatio = (double) garbageCharCount / content.length();
        return garbageRatio > 0.05;
    }
}
