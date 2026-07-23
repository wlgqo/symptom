package com.symptom.controller;

import com.symptom.entity.SysUser;
import com.symptom.entity.WarningModel;
import com.symptom.service.UserService;
import com.symptom.service.WarningService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.*;

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
    public String index(@RequestParam(defaultValue = "users") String tab,
                        HttpSession session, Model model) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (!"管理员".equals(user.getRole())) {
            return "redirect:/";
        }
        model.addAttribute("activeTab", tab);
        model.addAttribute("users", userService.findAll());
        model.addAttribute("logs", userService.getOperationLogs());
        model.addAttribute("menuItems", buildMenuItems());
        model.addAttribute("roles", buildRoles());
        model.addAttribute("dictItems", buildDictItems());
        model.addAttribute("notices", buildNotices());
        model.addAttribute("monitorStats", buildMonitorStats());
        return "admin/index";
    }

    @PostMapping("/user/save")
    public String saveUser(SysUser user, HttpSession session) {
        SysUser current = (SysUser) session.getAttribute("currentUser");
        if (!"管理员".equals(current.getRole())) {
            return "redirect:/";
        }
        userService.saveUser(user);
        return "redirect:/admin?tab=users";
    }

    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable Integer id, HttpSession session) {
        SysUser current = (SysUser) session.getAttribute("currentUser");
        if (!"管理员".equals(current.getRole())) {
            return "redirect:/";
        }
        userService.deleteUser(id);
        return "redirect:/admin?tab=users";
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

    private List<Map<String, String>> buildMenuItems() {
        List<Map<String, String>> items = new ArrayList<>();
        items.add(menuItem("监测驾驶舱", "/", "启用"));
        items.add(menuItem("发热呼吸道症候群", "/respiratory", "启用"));
        items.add(menuItem("发热伴出血症候群", "/hemorrhage", "启用"));
        items.add(menuItem("发热伴腹泻症候群", "/diarrhea", "启用"));
        items.add(menuItem("实时预警", "/warning/center", "启用"));
        items.add(menuItem("事件中心", "/event", "启用"));
        items.add(menuItem("主动监测病例", "/case/list", "启用"));
        items.add(menuItem("条件树组合分析", "/search", "启用"));
        items.add(menuItem("症候群主题库", "/theme", "启用"));
        items.add(menuItem("症候群配置", "/config", "启用"));
        items.add(menuItem("菜单配置", "/admin?tab=menu", "启用"));
        items.add(menuItem("用户配置", "/admin?tab=users", "启用"));
        return items;
    }

    private Map<String, String> menuItem(String name, String path, String status) {
        Map<String, String> m = new HashMap<>();
        m.put("name", name);
        m.put("path", path);
        m.put("status", status);
        return m;
    }

    private List<Map<String, String>> buildRoles() {
        List<Map<String, String>> roles = new ArrayList<>();
        roles.add(role("管理员", "系统全部功能，含系统管理模块", "3"));
        roles.add(role("业务人员", "监测、预警、病例、分析等业务功能", "5"));
        roles.add(role("浏览人员", "只读查看，不可修改病例", "2"));
        return roles;
    }

    private Map<String, String> role(String name, String desc, String count) {
        Map<String, String> r = new HashMap<>();
        r.put("name", name);
        r.put("desc", desc);
        r.put("count", count);
        return r;
    }

    private List<Map<String, String>> buildDictItems() {
        List<Map<String, String>> dicts = new ArrayList<>();
        dicts.add(dict("case_type", "病例类型", "疑似病例,确诊病例,临床诊断病例"));
        dicts.add(dict("risk_level", "风险等级", "低风险,中风险,高风险,待评估"));
        dicts.add(dict("warning_status", "预警状态", "待研判,已确认,处置中,已完成,已关闭"));
        dicts.add(dict("discover_type", "发现方式", "主动监测,医院报告,社区筛查,学校报告"));
        dicts.add(dict("event_status", "事件状态", "待核查,调查中,处置中,已完成,已关闭"));
        dicts.add(dict("syndrome_type", "症候群类型", "发热呼吸道症候群,发热伴出血症候群,发热伴腹泻症候群"));
        return dicts;
    }

    private Map<String, String> dict(String code, String name, String values) {
        Map<String, String> d = new HashMap<>();
        d.put("code", code);
        d.put("name", name);
        d.put("values", values);
        return d;
    }

    private List<Map<String, String>> buildNotices() {
        List<Map<String, String>> notices = new ArrayList<>();
        notices.add(notice("2026-07-20", "系统升级通知", "症候群监测系统 V1.0 已上线试运行，请各业务人员及时登录熟悉操作流程。", "已发布"));
        notices.add(notice("2026-07-15", "出血热监测专项", "阿坝州、甘孜州进入出血热高发季节，请加强农牧区病例监测与报告。", "已发布"));
        notices.add(notice("2026-07-10", "数据质量提醒", "请各医疗机构确保病例主诉、现病史、辅助检查等信息完整录入。", "已发布"));
        return notices;
    }

    private Map<String, String> notice(String date, String title, String content, String status) {
        Map<String, String> n = new HashMap<>();
        n.put("date", date);
        n.put("title", title);
        n.put("content", content);
        n.put("status", status);
        return n;
    }

    private Map<String, Object> buildMonitorStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("onlineUsers", 3);
        stats.put("todayLogins", userService.getOperationLogs().stream()
                .filter(l -> "用户登录".equals(l.getOperation())).count());
        stats.put("dbStatus", "正常");
        stats.put("apiStatus", "正常");
        stats.put("dataSync", "正常");
        Runtime rt = Runtime.getRuntime();
        long used = (rt.totalMemory() - rt.freeMemory()) / 1024 / 1024;
        long total = rt.totalMemory() / 1024 / 1024;
        stats.put("memoryUsage", used + "MB / " + total + "MB");
        return stats;
    }
}
