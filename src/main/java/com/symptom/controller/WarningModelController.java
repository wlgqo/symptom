package com.symptom.controller;

import com.symptom.entity.SysUser;
import com.symptom.entity.WarningModel;
import com.symptom.entity.WarningRecord;
import com.symptom.service.WarningService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/warning/model")
public class WarningModelController {

    private final WarningService warningService;

    public WarningModelController(WarningService warningService) {
        this.warningService = warningService;
    }

    @GetMapping
    public String index(@RequestParam(required = false) Integer id, Model model) {
        model.addAttribute("pageTitle", "预警模型中心");
        model.addAttribute("breadcrumb", "预警模型中心");
        List<WarningModel> models = warningService.getAllModels();
        model.addAttribute("models", models);
        model.addAttribute("modelStats", warningService.getModelStats());

        WarningModel selected = null;
        if (id != null) {
            selected = warningService.getModelById(id);
        } else if (!models.isEmpty()) {
            selected = models.get(0);
        }
        model.addAttribute("selected", selected);
        model.addAttribute("warningRegions", com.symptom.service.FilterOptionService.WARNING_REGIONS);
        model.addAttribute("warningTypes", com.symptom.service.FilterOptionService.WARNING_TYPES);
        if (selected != null) {
            model.addAttribute("modelWarnings", warningService.getRecordsByModelId(selected.getId()));
        }
        return "warning/model";
    }

    @PostMapping("/run")
    @ResponseBody
    public Map<String, Object> run(@RequestBody Map<String, Object> body) {
        Map<String, Object> result = new HashMap<>();
        Integer modelId = (Integer) body.get("modelId");
        String syndromeType = (String) body.get("syndromeType");
        List<WarningRecord> warnings = warningService.runWarningAnalysis(syndromeType, modelId);
        result.put("success", true);
        result.put("count", warnings.size());
        result.put("warnings", warnings);
        return result;
    }

    @PostMapping("/toggle")
    @ResponseBody
    public Map<String, Object> toggle(@RequestBody WarningModel model, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        Map<String, Object> result = new HashMap<>();
        if (!"管理员".equals(user.getRole()) && !"业务人员".equals(user.getRole())) {
            result.put("success", false);
            return result;
        }
        WarningModel existing = warningService.getModelById(model.getId());
        if (existing != null) {
            existing.setEnabled(model.getEnabled());
            warningService.updateModel(existing);
        }
        result.put("success", true);
        return result;
    }

    @PostMapping("/update")
    @ResponseBody
    public Map<String, Object> updateConfig(@RequestBody WarningModel model, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        Map<String, Object> result = new HashMap<>();
        if (!"管理员".equals(user.getRole())) {
            result.put("success", false);
            return result;
        }
        WarningModel existing = warningService.getModelById(model.getId());
        if (existing != null) {
            if (model.getDescription() != null) existing.setDescription(model.getDescription());
            if (model.getConfigJson() != null) existing.setConfigJson(model.getConfigJson());
            if (model.getRegionScope() != null) existing.setRegionScope(model.getRegionScope());
            if (model.getWarningType() != null) existing.setWarningType(model.getWarningType());
            if (model.getLevelThresholdJson() != null) existing.setLevelThresholdJson(model.getLevelThresholdJson());
            if (model.getEnabled() != null) existing.setEnabled(model.getEnabled());
            warningService.updateModel(existing);
        }
        result.put("success", true);
        return result;
    }
}
