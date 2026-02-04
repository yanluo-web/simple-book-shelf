package com.example.bookshelf.config;

import com.example.bookshelf.service.impl.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 注入自定义用户详情服务
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /**
     * 密码加密器（BCrypt 算法）
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 安全过滤链配置（已适配 BookController 路径改造，处理重定向问题）
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 关闭 CSRF（前后端一体项目，避免 POST 接口因 CSRF 校验失败重定向）
                .csrf().disable()
                // 关联自定义用户详情服务（从数据库查询用户，避免认证失败重定向）
                .userDetailsService(customUserDetailsService)
                // 权限请求配置（按优先级排序，精准控制路径权限）
                .authorizeHttpRequests()
                // 公开接口：无需登录即可访问（避免重定向到登录页）
                .antMatchers("/login", "/h2-console/**", "/static/**").permitAll()
                // 管理员专属接口：需 ADMIN 角色，否则重定向到登录/403
                .antMatchers("/admin/**", "/shelf/create","/user/manage").hasRole("ADMIN")
                // 可选：显式配置 /book/** 接口（普通用户登录后可访问，与 anyRequest 效果一致，更清晰）
                .antMatchers("/book/**","/read/**","/shelf/**").authenticated()
                // 其他所有接口：登录后即可访问（覆盖 /book/** 等普通接口，避免未授权重定向）
                .anyRequest().authenticated()
                // 登录配置（处理登录相关重定向，避免异常）
                .and()
                .formLogin()
                .loginPage("/login") // 自定义登录页路径（不存在则 404，需确保有对应 Controller）
                .defaultSuccessUrl("/index", true) // 登录成功后强制跳转首页（避免重定向到原请求接口出现异常）
                .permitAll()
                // 退出登录配置（处理退出后的重定向）
                .and()
                .logout()
                .logoutSuccessUrl("/login") // 退出成功后跳转登录页（清除会话，避免权限残留）
                .permitAll()
                // 会话配置（单用户登录，避免多端登录导致的重定向/权限异常）
                .and()
                .sessionManagement()
                .maximumSessions(1);

        // 允许 H2 控制台框架页面（否则无法正常访问 H2 控制台，出现空白/重定向异常）
        http.headers().frameOptions().disable();

        return http.build();
    }
}