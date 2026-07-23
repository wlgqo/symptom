package com.symptom.controller;

import com.symptom.entity.MonitorIndicator;
import com.symptom.entity.SymptomTerm;
import com.symptom.service.MonitorIndicatorService;
import com.symptom.entity.SyndromeConfig;
import com.symptom.entity.SysUser;
import com.symptom.entity.WarningModel;
import com.symptom.service.SyndromeConfigService;
import com.symptom.service.SymptomTermService;
import com.symptom.service.WarningService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/config")
public class MonitorConfigController {

    private final SyndromeConfigService syndromeConfigService;
    private final WarningService warningService;
    private final SymptomTermService symptomTermService;
    private final MonitorIndicatorService monitorIndicatorService;

    public MonitorConfigController(SyndromeConfigService syndromeConfigService,
                                   WarningService warningService,
                                   SymptomTermService symptomTermService,
                                   MonitorIndicatorService monitorIndicatorService) {
        this.syndromeConfigService = syndromeConfigService;
        this.warningService = warningService;
        this.symptomTermService = symptomTermService;
        this.monitorIndicatorService = monitorIndicatorService;
    }

    @GetMapping
    public String index(@RequestParam(required = false) Integer syndromeId,
                        @RequestParam(required = false) Integer modelId,
                        Model model, HttpSession session) {
        model.addAttribute("pageTitle", "症候群配置");
        model.addAttribute("breadcrumb", "症候群配置");
        model.addAttribute("syndromeConfigs", syndromeConfigService.findAll());
        model.addAttribute("models", warningService.getAllModels());
        model.addAttribute("symptomTerms", symptomTermService.findAll());
        model.addAttribute("indicators", monitorIndicatorService.findAll());

        SyndromeConfig selectedSyndrome = null;
        if (syndromeId != null) {
            selectedSyndrome = syndromeConfigService.findById(syndromeId);
        } else {
            List<SyndromeConfig> configs = syndromeConfigService.findAll();
            if (!configs.isEmpty()) {
                selectedSyndrome = configs.get(0);
            }
        }
        model.addAttribute("selectedSyndrome", selectedSyndrome);

        WarningModel selectedModel = null;
        if (modelId != null) {
            selectedModel = warningService.getModelById(modelId);
        } else {
            List<WarningModel> allModels = warningService.getAllModels();
            if (!allModels.isEmpty()) {
                selectedModel = allModels.get(0);
            }
        }
        model.addAttribute("selectedModel", selectedModel);
        return "config/index";
    }

    @PostMapping("/syndrome/update")
    @ResponseBody
    public Map<String, Object> updateSyndrome(@RequestBody SyndromeConfig config, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        Map<String, Object> result = new HashMap<>();
        if (!"管理员".equals(user.getRole())) {
            result.put("success", false);
            return result;
        }
        syndromeConfigService.update(config);
        result.put("success", true);
        return result;
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

    @PostMapping("/symptom/save")
    @ResponseBody
    public Map<String, Object> saveSymptom(@RequestBody SymptomTerm term, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        Map<String, Object> result = new HashMap<>();
        if (!"管理员".equals(user.getRole())) {
            result.put("success", false);
            return result;
        }
        symptomTermService.save(term);
        result.put("success", true);
        return result;
    }

    @PostMapping("/symptom/delete")
    @ResponseBody
    public Map<String, Object> deleteSymptom(@RequestParam Integer id, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        Map<String, Object> result = new HashMap<>();
        if (!"管理员".equals(user.getRole())) {
            result.put("success", false);
            return result;
        }
        symptomTermService.delete(id);
        result.put("success", true);
        return result;
    }

    @PostMapping("/indicator/save")
    @ResponseBody
    public Map<String, Object> saveIndicator(@RequestBody MonitorIndicator indicator, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        Map<String, Object> result = new HashMap<>();
        if (!"管理员".equals(user.getRole())) {
            result.put("success", false);
            return result;
        }
        monitorIndicatorService.save(indicator);
        result.put("success", true);
        return result;
    }

    @PostMapping("/indicator/delete")
    @ResponseBody
    public Map<String, Object> deleteIndicator(@RequestParam Integer id, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        Map<String, Object> result = new HashMap<>();
        if (!"管理员".equals(user.getRole())) {
            result.put("success", false);
            return result;
        }
        monitorIndicatorService.delete(id);
        result.put("success", true);
        return result;
    }
}
