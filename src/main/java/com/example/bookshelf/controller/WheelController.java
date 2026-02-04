package com.example.bookshelf.controller;

import com.example.bookshelf.entity.GameWheel;
import com.example.bookshelf.entity.GameWheelItem;
import com.example.bookshelf.service.GameWheelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/entertainment/wheel")
public class WheelController {

    @Autowired
    private GameWheelService gameWheelService;

    /**
     * 转盘首页（选择转盘）
     * @param model
     * @return
     */
    @GetMapping("")
    public String wheelIndex(Model model) {
        List<GameWheel> wheels = gameWheelService.getAllWheels();
        model.addAttribute("wheels", wheels);
        return "entertainment/wheel/index";
    }

    /**
     * 进入指定转盘页面
     * @param wheelId
     * @param model
     * @return
     */
    @GetMapping("/{wheelId}")
    public String wheelPage(@PathVariable Long wheelId, Model model) {
        GameWheel wheel = gameWheelService.getWheelById(wheelId);
        List<GameWheelItem> items = gameWheelService.getItemsByWheelId(wheelId);
        model.addAttribute("wheel", wheel);
        model.addAttribute("items", items);
        return "entertainment/wheel/detail";
    }

    /**
     * 随机抽取选项（AJAX 接口）
     */
    @GetMapping("/random/{wheelId}")
    @ResponseBody
    public GameWheelItem randomItem(@PathVariable Long wheelId) {
        return gameWheelService.randomItem(wheelId);
    }
}
