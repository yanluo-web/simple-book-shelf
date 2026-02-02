package com.example.bookshelf.util;

import com.example.bookshelf.mapper.UserMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtil {

    /**
     * 获取当前登录用户的用户名
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }

    /**
     * 获取当前登录的用户ID
     * @return
     */
    public static Long getCurrentUserId(){
        // 关键修改：通过 SpringContextUtil 获取 UserMapper 实例（非 null）
        UserMapper userMapper = SpringContextUtil.getBean(UserMapper.class);
        String username = getCurrentUsername();
        // 增加空值判断，避免 username 为 null 时 MyBatis 报错
        if (username == null) {
            return null;
        }
        return userMapper.selectUserIdByUsername(username);
    }

    /**
     * 判断当前登录用户是否为管理员
     */
    public static boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        // 判断是否包含 ADMIN 角色
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}