package com.example.bookshelf.controller;

import com.example.bookshelf.entity.User;
import com.example.bookshelf.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    /**
     * 进入用户管理页面（仅管理员可访问，此处简化权限控制，后续可整合 Spring Security）
     */
    @GetMapping("/userManage")
    public String userManage(Model model) {
        // 1. 查询所有用户
        List<User> userList = userService.getAllUsers();
        model.addAttribute("userList", userList);

        // 2. 传递管理员标识（用于导航栏显示）
        model.addAttribute("isAdmin", true); // 此处简化，实际应从登录用户信息中获取

        return "/user/userManage";
    }

    /**
     * 新增用户
     */
    @PostMapping("/add")
    public String addUser(User user, RedirectAttributes redirectAttributes) {
        boolean success = userService.addUser(user);
        if (success) {
            redirectAttributes.addFlashAttribute("msg", "用户新增成功！");
        } else {
            redirectAttributes.addFlashAttribute("error", "用户新增失败！");
        }
        return "redirect:/user/userManage";
    }

    /**
     * 修改用户（不含密码）
     */
    @PostMapping("/update")
    public String updateUser(User user, RedirectAttributes redirectAttributes) {
        boolean success = userService.updateUser(user);
        if (success) {
            redirectAttributes.addFlashAttribute("msg", "用户修改成功！");
        } else {
            redirectAttributes.addFlashAttribute("error", "用户修改失败！");
        }
        return "redirect:/user/userManage";
    }

    /**
     * 修改用户密码
     */
    @PostMapping("/update/password")
    public String updateUserPassword(@RequestParam Long id, @RequestParam String newPassword,
                                     RedirectAttributes redirectAttributes) {
        boolean success = userService.updateUserPassword(id, newPassword);
        if (success) {
            redirectAttributes.addFlashAttribute("msg", "密码修改成功！");
        } else {
            redirectAttributes.addFlashAttribute("error", "密码修改失败！");
        }
        return "redirect:/user/userManage";
    }

    /**
     * 删除用户
     */
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean success = userService.deleteUser(id);
        if (success) {
            redirectAttributes.addFlashAttribute("msg", "用户删除成功！");
        } else {
            redirectAttributes.addFlashAttribute("error", "用户删除失败（禁止删除管理员）！");
        }
        return "redirect:/user/userManage";
    }
}