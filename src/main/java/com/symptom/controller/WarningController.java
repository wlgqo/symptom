package com.symptom.controller;

import com.symptom.common.PageResult;
import com.symptom.entity.*;
import com.symptom.service.CaseService;
import com.symptom.service.DataScopeService;
import com.symptom.service.WarningService;
import com.symptom.util.QueryParamUtil;
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
    private final DataScopeService dataScopeService;

    public WarningController(WarningService warningService, CaseService caseService,
                             DataScopeService dataScopeService) {
        this.warningService = warningService;
        this.caseService = caseService;
        this.dataScopeService = dataScopeService;
    }

    @GetMapping("/center")
    public String center(@RequestParam(required = false) Integer id,
                         @RequestParam(required = false) String syndromeType,
                         @RequestParam(required = false) String status,
                         @RequestParam(required = false) String startDate,
                         @RequestParam(required = false) String endDate,
                         @RequestParam(required = false, defaultValue = "1") Integer page,
                         @RequestParam(required = false, defaultValue = "15") Integer pageSize,
                         HttpSession session,
                         Model model) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        Map<String, Object> params = new HashMap<>();
        params.put("syndromeType", syndromeType);
        params.put("status", status);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        dataScopeService.applyWarningScope(params, user);

        PageResult<WarningRecord> pageResult = warningService.searchPage(params, page, pageSize);
        List<WarningRecord> records = pageResult.getRecords();

        model.addAttribute("pageTitle", "预警中心");
        model.addAttribute("breadcrumb", "预警中心");
        model.addAttribute("records", records);
        model.addAttribute("pageResult", pageResult);
        model.addAttribute("filterSyndrome", syndromeType);
        model.addAttribute("filterStatus", status);
        model.addAttribute("filterStartDate", startDate);
        model.addAttribute("filterEndDate", endDate);
        model.addAttribute("scopeDistrict", dataScopeService.scopeDistrict(user));

        WarningRecord selected = null;
        if (id != null) {
            selected = warningService.getRecordById(id);
            if (selected != null && dataScopeService.hasDistrictScope(user)
                    && !user.getDistrictScope().equals(selected.getDistrict())) {
                selected = null;
            }
        } else if (!records.isEmpty()) {
            selected = records.get(0);
        }
        model.addAttribute("selected", selected);

        if (selected != null) {
            model.addAttribute("notifications", warningService.getNotifications(selected.getId()));
            model.addAttribute("disposals", warningService.getDisposals(selected.getId()));
            Map<String, Object> caseFilter = QueryParamUtil.baseFilter(selected.getSyndromeType(), null, null, null, null);
            dataScopeService.applyCaseScope(caseFilter, user);
            model.addAttribute("relatedCases", caseService.searchLimited(caseFilter, 5));
            if (selected.getModelId() != null) {
                model.addAttribute("model", warningService.getModelById(selected.getModelId()));
            }
        }

        Map<String, Object> pendingScope = new HashMap<>();
        dataScopeService.applyWarningScope(pendingScope, user);
        model.addAttribute("pendingCount", warningService.countScopedPending(pendingScope));
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
