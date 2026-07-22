package com.symptom.controller;

import com.symptom.entity.SysUser;
import com.symptom.mapper.CaseInfoMapper;
import com.symptom.mapper.WarningRecordMapper;
import com.symptom.service.AnalysisService;
import com.symptom.service.CaseService;
import com.symptom.service.WarningService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class DashboardController {

    private final CaseService caseService;
    private final WarningService warningService;
    private final AnalysisService analysisService;
    private final CaseInfoMapper caseInfoMapper;
    private final WarningRecordMapper warningRecordMapper;

    public DashboardController(CaseService caseService, WarningService warningService,
                               AnalysisService analysisService, CaseInfoMapper caseInfoMapper,
                               WarningRecordMapper warningRecordMapper) {
        this.caseService = caseService;
        this.warningService = warningService;
        this.analysisService = analysisService;
        this.caseInfoMapper = caseInfoMapper;
        this.warningRecordMapper = warningRecordMapper;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "首页驾驶舱");
        Map<String, Object> stats = caseService.getDashboardStats();
        stats.put("warningCount", warningRecordMapper.countPending());
        model.addAttribute("stats", stats);
        model.addAttribute("timeData", analysisService.getTimeDistribution(null, "month"));
        model.addAttribute("districtData", analysisService.getDistrictDistribution(null));
        model.addAttribute("recentWarnings", warningService.getAllRecords().isEmpty() ?
                java.util.Collections.emptyList() :
                warningService.getAllRecords().subList(0, Math.min(5, warningService.getAllRecords().size())));
        return "dashboard";
    }
}
