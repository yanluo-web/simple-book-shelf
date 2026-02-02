package com.example.bookshelf.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;


@Component
public class FileUtil {

    // 从配置文件读取存储目录相对路径
    @Value("${book.storage.path}")
    private String bookStorageRelativePath;

    // 获取实际存储目录（项目根目录 + 配置的相对路径）
    private String getBookStorageAbsolutePath() {
        // 获取项目根目录
        String projectRootPath = System.getProperty("user.dir");
        // 拼接绝对路径
        return projectRootPath + File.separator + bookStorageRelativePath;
    }

    // 初始化存储目录（若不存在则创建）
    public void initStorageDir() {
        String absolutePath = getBookStorageAbsolutePath();
        File dir = new File(absolutePath);
        if (!dir.exists()) {
            // 递归创建多级目录（避免父目录不存在报错）
            dir.mkdirs();
        }
    }

    // 上传书籍文件（生成唯一文件名，避免重复）
    public String uploadBookFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空！");
        }
        initStorageDir();

        // 获取原文件名和文件格式
        String originalFileName = file.getOriginalFilename();
        assert originalFileName != null;
        if (!originalFileName.contains(".")) {
            throw new IllegalArgumentException("上传文件格式不合法！");
        }
        String fileFormat = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();

        // 仅支持 TXT 和 EPUB 格式
        if (!".txt".equals(fileFormat) && !".epub".equals(fileFormat)) {
            throw new IllegalArgumentException("仅支持 TXT 和 EPUB 格式书籍上传！");
        }

        // 生成唯一文件名（UUID + 格式）
        String uniqueFileName = UUID.randomUUID().toString() + fileFormat;
        // 获取绝对存储路径
        String absolutePath = getBookStorageAbsolutePath();
        String targetFilePath = absolutePath + File.separator + uniqueFileName;
        File targetFile = new File(targetFilePath);

        // 上传文件（transferTo 方法需要目标目录已存在，否则报错）
        file.transferTo(targetFile);

        // 返回文件绝对路径（或根据业务返回相对路径）
        return targetFilePath;
    }

    /**
     * 读取 TXT 书籍内容（EPUB 格式需额外引入解析依赖，先实现 TXT 简单读取）
     */
    public String readTxtBookContent(File bookFile) throws IOException {
        if (!bookFile.exists() || !bookFile.canRead()) {
            throw new FileNotFoundException("书籍文件不存在或无法读取");
        }
        // JDK 8 兼容方案：使用 BufferedReader 逐行读取
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(bookFile), StandardCharsets.UTF_8)
        )) {
            String line;
            while ((line = br.readLine()) != null) {
                // 保留每行内容，添加换行符（避免所有内容连在一起）
                contentBuilder.append(line).append("\n");
            }
        }

        return contentBuilder.toString();
    }
}