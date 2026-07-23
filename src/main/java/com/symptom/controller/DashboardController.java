package com.symptom.controller;

import com.symptom.entity.SurveillanceEvent;
import com.symptom.entity.SysUser;
import com.symptom.entity.WarningRecord;
import com.symptom.mapper.CaseInfoMapper;
import com.symptom.mapper.WarningRecordMapper;
import com.symptom.service.AnalysisService;
import com.symptom.service.CaseService;
import com.symptom.service.EventService;
import com.symptom.service.SyndromeConfigService;
import com.symptom.service.WarningService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    private final CaseService caseService;
    private final WarningService warningService;
    private final AnalysisService analysisService;
    private final CaseInfoMapper caseInfoMapper;
    private final WarningRecordMapper warningRecordMapper;
    private final SyndromeConfigService syndromeConfigService;
    private final EventService eventService;

    public DashboardController(CaseService caseService, WarningService warningService,
                               AnalysisService analysisService, CaseInfoMapper caseInfoMapper,
                               WarningRecordMapper warningRecordMapper,
                               SyndromeConfigService syndromeConfigService,
                               EventService eventService) {
        this.caseService = caseService;
        this.warningService = warningService;
        this.analysisService = analysisService;
        this.caseInfoMapper = caseInfoMapper;
        this.warningRecordMapper = warningRecordMapper;
        this.syndromeConfigService = syndromeConfigService;
        this.eventService = eventService;
    }

    @GetMapping("/")
    public String dashboard(@RequestParam(required = false) String syndromeType,
                            @RequestParam(required = false) String district,
                            @RequestParam(required = false, defaultValue = "30") Integer days,
                            Model model) {
        model.addAttribute("pageTitle", "监测驾驶舱");
        model.addAttribute("breadcrumb", "监测驾驶舱");
        model.addAttribute("filterSyndrome", syndromeType);
        model.addAttribute("filterDistrict", district);
        model.addAttribute("filterDays", days);

        Map<String, Object> stats = caseService.getDashboardStats(syndromeType, district, days);
        stats.put("warningCount", warningRecordMapper.countPending());
        model.addAttribute("stats", stats);
        model.addAttribute("timeData", analysisService.getTimeDistribution(syndromeType, "month", district, days));
        model.addAttribute("districtData", analysisService.getDistrictDistribution(syndromeType, district, days));

        List<WarningRecord> allWarnings = warningService.searchRecords(syndromeType, null);
        model.addAttribute("recentWarnings", allWarnings.isEmpty() ?
                java.util.Collections.emptyList() :
                allWarnings.subList(0, Math.min(5, allWarnings.size())));

        Map<String, Object> filter = caseService.buildFilterParamsPublic(syndromeType, district, days);
        Map<String, Object> highRiskFilter = new HashMap<>(filter);
        highRiskFilter.put("riskLevel", "高风险");
        model.addAttribute("highRiskCases", caseService.searchLimited(highRiskFilter, 5));

        model.addAttribute("syndromeConfigs", syndromeConfigService.findAll());

        List<SurveillanceEvent> allEvents = eventService.findAll();
        if (district != null && !district.isEmpty()) {
            allEvents = allEvents.stream()
                    .filter(e -> district.equals(e.getDistrict()))
                    .collect(Collectors.toList());
        }
        model.addAttribute("recentEvents", allEvents.isEmpty() ?
                java.util.Collections.emptyList() :
                allEvents.subList(0, Math.min(5, allEvents.size())));
        model.addAttribute("pendingEvents", eventService.countPending());
        return "dashboard";
    }
}
