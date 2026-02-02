package com.example.bookshelf;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@MapperScan("com.example.bookshelf.mapper") // 扫描 MyBatis Mapper 接口
@RestController
public class BookShelfApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookShelfApplication.class, args);
        System.out.println("极简书架系统启动成功！访问地址：http://localhost:8088");
        System.out.println("默认管理员账号：admin，密码：123456");
        System.out.println("H2数据库访问地址：http://localhost:8088/h2-console");
        System.out.println("默认管理员账号：sa，密码：123456");
    }

    // 测试接口
    @GetMapping("/")
    public String index() {
        return "欢迎使用极简书架系统！<br/>请访问 /book 进入书架（默认账号：admin/123456）";
    }
}