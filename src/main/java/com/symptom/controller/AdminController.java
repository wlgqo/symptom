package com.symptom.controller;

import com.symptom.entity.SysUser;
import com.symptom.entity.WarningModel;
import com.symptom.service.UserService;
import com.symptom.service.WarningService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final WarningService warningService;

    public AdminController(UserService userService, WarningService warningService) {
        this.userService = userService;
        this.warningService = warningService;
    }

    @GetMapping
    public String index(HttpSession session, Model model) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (!"管理员".equals(user.getRole())) {
            return "redirect:/";
        }
        model.addAttribute("users", userService.findAll());
        model.addAttribute("models", warningService.getAllModels());
        model.addAttribute("logs", userService.getOperationLogs());
        return "admin/index";
    }

    @PostMapping("/user/save")
    public String saveUser(SysUser user, HttpSession session) {
        SysUser current = (SysUser) session.getAttribute("currentUser");
        if (!"管理员".equals(current.getRole())) {
            return "redirect:/";
        }
        userService.saveUser(user);
        return "redirect:/admin";
    }

    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable Integer id, HttpSession session) {
        SysUser current = (SysUser) session.getAttribute("currentUser");
        if (!"管理员".equals(current.getRole())) {
            return "redirect:/";
        }
        userService.deleteUser(id);
        return "redirect:/admin";
    }

    @PostMapping("/model/update")
    @ResponseBody
    public Map<String, Object> updateModel(@RequestBody WarningModel model, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        Map<String, Object> result = new HashMap<>();
        if (!"管理员".equals(user.getRole())) {
            result.put("success", false);
            return result;
        }
        warningService.updateModel(model);
        result.put("success", true);
        return result;
    }
}
