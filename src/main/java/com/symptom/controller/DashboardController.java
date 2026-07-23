package com.symptom.controller;

import com.symptom.entity.SurveillanceEvent;
import com.symptom.entity.SysUser;
import com.symptom.entity.WarningRecord;
import com.symptom.mapper.WarningRecordMapper;
import com.symptom.service.AnalysisService;
import com.symptom.service.CaseService;
import com.symptom.service.DataScopeService;
import com.symptom.service.EventService;
import com.symptom.service.SyndromeConfigService;
import com.symptom.service.WarningService;
import com.symptom.service.FilterOptionService;
import com.symptom.util.FilterViewHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    private final CaseService caseService;
    private final WarningService warningService;
    private final AnalysisService analysisService;
    private final WarningRecordMapper warningRecordMapper;
    private final SyndromeConfigService syndromeConfigService;
    private final EventService eventService;
    private final DataScopeService dataScopeService;
    private final FilterOptionService filterOptionService;

    public DashboardController(CaseService caseService, WarningService warningService,
                               AnalysisService analysisService,
                               WarningRecordMapper warningRecordMapper,
                               SyndromeConfigService syndromeConfigService,
                               EventService eventService,
                               DataScopeService dataScopeService,
                               FilterOptionService filterOptionService) {
        this.caseService = caseService;
        this.warningService = warningService;
        this.analysisService = analysisService;
        this.warningRecordMapper = warningRecordMapper;
        this.syndromeConfigService = syndromeConfigService;
        this.eventService = eventService;
        this.dataScopeService = dataScopeService;
        this.filterOptionService = filterOptionService;
    }

    @GetMapping("/")
    public String dashboard(@RequestParam(required = false) String syndromeType,
                            @RequestParam(required = false) String district,
                            @RequestParam(required = false) String hospital,
                            @RequestParam(required = false) String startDate,
                            @RequestParam(required = false) String endDate,
                            @RequestParam(required = false, defaultValue = "90") Integer days,
                            HttpSession session,
                            Model model) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        Map<String, Object> filter = FilterViewHelper.buildScopedFilter(dataScopeService, user,
                syndromeType, district, hospital, startDate, endDate, days);

        model.addAttribute("pageTitle", "监测驾驶舱");
        model.addAttribute("breadcrumb", "监测驾驶舱");
        model.addAttribute("filterSyndrome", syndromeType);
        FilterViewHelper.addRegionHospitalModel(model, filterOptionService, dataScopeService, user, district, hospital);
        model.addAttribute("filterStartDate", startDate);
        model.addAttribute("filterEndDate", endDate);
        model.addAttribute("filterDays", days);

        Map<String, Object> stats = caseService.getDashboardStats(filter);
        Map<String, Object> warningScope = new HashMap<>(filter);
        dataScopeService.applyWarningScope(warningScope, user);
        stats.put("warningCount", warningRecordMapper.countScoped(warningScope));
        model.addAttribute("stats", stats);

        model.addAttribute("timeData", analysisService.getTimeDistribution(filter, "month"));
        model.addAttribute("districtData", analysisService.getDistrictDistribution(filter));

        List<WarningRecord> allWarnings = warningService.searchScoped(syndromeType, null,
                (String) filter.get("district"), (String) filter.get("hospital"), startDate, endDate, user);
        model.addAttribute("recentWarnings", allWarnings.isEmpty() ?
                java.util.Collections.emptyList() :
                allWarnings.subList(0, Math.min(5, allWarnings.size())));

        Map<String, Object> highRiskFilter = new HashMap<>(filter);
        highRiskFilter.put("riskLevel", "高风险");
        model.addAttribute("highRiskCases", caseService.searchLimited(highRiskFilter, 5));

        model.addAttribute("syndromeConfigs", syndromeConfigService.findAll());

        List<SurveillanceEvent> allEvents = eventService.findAll();
        if (dataScopeService.isCityWide(user)) {
            java.util.Set<String> chengdu = new java.util.HashSet<>(FilterOptionService.getChengduDistricts());
            allEvents = allEvents.stream()
                    .filter(e -> e.getDistrict() != null && chengdu.contains(e.getDistrict()))
                    .collect(Collectors.toList());
        } else {
            String effectiveDistrict = dataScopeService.hasDistrictScope(user)
                    ? user.getDistrictScope() : (String) filter.get("district");
            if (effectiveDistrict != null && !effectiveDistrict.isEmpty()) {
                allEvents = allEvents.stream()
                        .filter(e -> effectiveDistrict.equals(e.getDistrict()))
                        .collect(Collectors.toList());
            }
        }
        model.addAttribute("recentEvents", allEvents.isEmpty() ?
                java.util.Collections.emptyList() :
                allEvents.subList(0, Math.min(5, allEvents.size())));
        model.addAttribute("pendingEvents", eventService.countPending());
        return "dashboard";
    }
}
