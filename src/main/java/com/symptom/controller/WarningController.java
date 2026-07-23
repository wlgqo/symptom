package com.symptom.controller;

import com.symptom.entity.*;
import com.symptom.service.CaseService;
import com.symptom.service.WarningService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/warning")
public class WarningController {

    private final WarningService warningService;
    private final CaseService caseService;

    public WarningController(WarningService warningService, CaseService caseService) {
        this.warningService = warningService;
        this.caseService = caseService;
    }

    @GetMapping("/center")
    public String center(@RequestParam(required = false) Integer id,
                         @RequestParam(required = false) String syndromeType,
                         @RequestParam(required = false) String status,
                         Model model) {
        model.addAttribute("pageTitle", "预警中心");
        model.addAttribute("breadcrumb", "预警中心");
        List<WarningRecord> records = warningService.searchRecords(syndromeType, status);
        model.addAttribute("records", records);
        model.addAttribute("filterSyndrome", syndromeType);
        model.addAttribute("filterStatus", status);

        WarningRecord selected = null;
        if (id != null) {
            selected = warningService.getRecordById(id);
        } else if (!records.isEmpty()) {
            selected = records.get(0);
        }
        model.addAttribute("selected", selected);

        if (selected != null) {
            model.addAttribute("notifications", warningService.getNotifications(selected.getId()));
            model.addAttribute("disposals", warningService.getDisposals(selected.getId()));
            model.addAttribute("relatedCases", caseService.findBySyndromeType(selected.getSyndromeType()));
            if (selected.getModelId() != null) {
                model.addAttribute("model", warningService.getModelById(selected.getModelId()));
            }
        }
        model.addAttribute("pendingCount", warningService.countPending());
        return "warning/center";
    }

    @PostMapping("/action")
    @ResponseBody
    public Map<String, Object> action(@RequestBody Map<String, Object> body, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        Map<String, Object> result = new HashMap<>();
        Integer warningId = toInteger(body.get("warningId"));
        String action = (String) body.get("action");
        String comment = (String) body.getOrDefault("comment", "");

        if (warningId == null || action == null) {
            result.put("success", false);
            result.put("message", "参数不完整");
            return result;
        }

        warningService.processAction(warningId, action, user.getRealName(), comment);
        result.put("success", true);
        return result;
    }

    @PostMapping("/notify")
    @ResponseBody
    public Map<String, Object> notify(@RequestBody Map<String, Object> body) {
        Map<String, Object> result = new HashMap<>();
        Integer warningId = toInteger(body.get("warningId"));
        if (warningId == null) {
            result.put("success", false);
            result.put("message", "预警ID无效");
            return result;
        }
        String target = (String) body.getOrDefault("target", "疾控业务人员");
        String method = (String) body.getOrDefault("method", "站内消息");
        warningService.sendNotification(warningId, target, method);
        result.put("success", true);
        return result;
    }

    private Integer toInteger(Object value) {
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).intValue();
        if (value instanceof String && !((String) value).isEmpty()) {
            return Integer.parseInt((String) value);
        }
        return null;
    }
}
