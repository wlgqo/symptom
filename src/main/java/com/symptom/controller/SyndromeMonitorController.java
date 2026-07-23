package com.symptom.controller;

import com.symptom.common.PageResult;
import com.symptom.entity.CaseInfo;
import com.symptom.entity.SysUser;
import com.symptom.entity.WarningRecord;
import com.symptom.service.*;
import com.symptom.util.QueryParamUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class SyndromeMonitorController {

    private static final Map<String, SyndromeMeta> SYNDROMES = new LinkedHashMap<>();

    static {
        SYNDROMES.put("respiratory", new SyndromeMeta("发热呼吸道症候群", "respiratory"));
        SYNDROMES.put("hemorrhage", new SyndromeMeta("发热伴出血症候群", "hemorrhage"));
        SYNDROMES.put("diarrhea", new SyndromeMeta("发热伴腹泻症候群", "diarrhea"));
        SYNDROMES.put("rash", new SyndromeMeta("发热出疹症候群", "rash"));
        SYNDROMES.put("encephalitis", new SyndromeMeta("脑炎脑膜炎症候群", "encephalitis"));
        SYNDROMES.put("fuo", new SyndromeMeta("不明原因发热症候群", "fuo"));
    }

    private final CaseService caseService;
    private final AnalysisService analysisService;
    private final WarningService warningService;
    private final SyndromeConfigService syndromeConfigService;
    private final DataScopeService dataScopeService;
    private final FilterOptionService filterOptionService;

    public SyndromeMonitorController(CaseService caseService, AnalysisService analysisService,
                                     WarningService warningService,
                                     SyndromeConfigService syndromeConfigService,
                                     DataScopeService dataScopeService,
                                     FilterOptionService filterOptionService) {
        this.caseService = caseService;
        this.analysisService = analysisService;
        this.warningService = warningService;
        this.syndromeConfigService = syndromeConfigService;
        this.dataScopeService = dataScopeService;
        this.filterOptionService = filterOptionService;
    }

    @GetMapping({"/respiratory", "/hemorrhage", "/diarrhea", "/rash", "/encephalitis", "/fuo"})
    public String index(@RequestParam(required = false) String district,
                        @RequestParam(required = false) String hospital,
                        @RequestParam(required = false) String startDate,
                        @RequestParam(required = false) String endDate,
                        @RequestParam(required = false, defaultValue = "90") Integer days,
                        @RequestParam(required = false, defaultValue = "1") Integer casePage,
                        @RequestParam(required = false, defaultValue = "1") Integer warningPage,
                        @RequestParam(required = false) String tab,
                        HttpSession session,
                        Model model,
                        javax.servlet.http.HttpServletRequest request) {
        String code = resolveCode(request.getRequestURI());
        return renderMonitor(code, district, hospital, startDate, endDate, days, casePage, warningPage, tab, session, model);
    }

    @PostMapping({"/respiratory/warning/run", "/hemorrhage/warning/run", "/diarrhea/warning/run",
            "/rash/warning/run", "/encephalitis/warning/run", "/fuo/warning/run"})
    @ResponseBody
    public Map<String, Object> runWarning(@RequestParam Integer modelId,
                                          javax.servlet.http.HttpServletRequest request) {
        String code = resolveCode(request.getRequestURI());
        SyndromeMeta meta = SYNDROMES.get(code);
        List<WarningRecord> records = warningService.runWarningAnalysis(meta.syndromeType, modelId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("count", records.size());
        result.put("records", records);
        return result;
    }

    @PostMapping({"/respiratory/warning/handle", "/hemorrhage/warning/handle", "/diarrhea/warning/handle",
            "/rash/warning/handle", "/encephalitis/warning/handle", "/fuo/warning/handle"})
    @ResponseBody
    public Map<String, Object> handleWarning(@RequestParam Integer id, @RequestParam String result,
                                             HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        warningService.handleWarning(id, user.getRealName(), result);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return resp;
    }

    @GetMapping({"/respiratory/api/time", "/hemorrhage/api/time", "/diarrhea/api/time",
            "/rash/api/time", "/encephalitis/api/time", "/fuo/api/time"})
    @ResponseBody
    public Map<String, Object> timeData(@RequestParam(defaultValue = "day") String groupBy,
                                        @RequestParam(required = false) String district,
                                        @RequestParam(required = false) String hospital,
                                        @RequestParam(required = false) String startDate,
                                        @RequestParam(required = false) String endDate,
                                        @RequestParam(required = false, defaultValue = "90") Integer days,
                                        HttpSession session,
                                        javax.servlet.http.HttpServletRequest request) {
        String code = resolveCode(request.getRequestURI());
        SyndromeMeta meta = SYNDROMES.get(code);
        Map<String, Object> filter = buildFilter(session, meta.syndromeType, district, hospital, startDate, endDate, days);
        return analysisService.getTimeDistribution(filter, groupBy);
    }

    private String renderMonitor(String code, String district, String hospital, String startDate, String endDate, Integer days,
                                 Integer casePage, Integer warningPage, String tab,
                                 HttpSession session, Model model) {
        SyndromeMeta meta = SYNDROMES.get(code);
        if (meta == null) {
            return "redirect:/";
        }
        SysUser user = (SysUser) session.getAttribute("currentUser");
        Map<String, Object> filter = buildFilter(session, meta.syndromeType, district, hospital, startDate, endDate, days);

        model.addAttribute("pageTitle", meta.syndromeType + "监测预警");
        model.addAttribute("breadcrumb", meta.syndromeType);
        model.addAttribute("syndromeCode", code);
        model.addAttribute("syndromeType", meta.syndromeType);
        model.addAttribute("activeMenu", code);
        com.symptom.util.FilterViewHelper.addRegionHospitalModel(model, filterOptionService, dataScopeService, user, district, hospital);
        model.addAttribute("filterStartDate", startDate);
        model.addAttribute("filterEndDate", endDate);
        model.addAttribute("filterDays", days);
        model.addAttribute("activeTab", tab != null ? tab : "distribution");

        model.addAttribute("timeDataDay", analysisService.getTimeDistribution(filter, "day"));
        model.addAttribute("timeDataWeek", analysisService.getTimeDistribution(filter, "week"));
        model.addAttribute("timeDataMonth", analysisService.getTimeDistribution(filter, "month"));
        model.addAttribute("districtData", analysisService.getDistrictDistribution(filter));
        model.addAttribute("populationData", analysisService.getPopulationDistribution(filter));
        model.addAttribute("clinicalData", analysisService.getClinicalFeatures(filter));
        model.addAttribute("severeDeathData", analysisService.getSevereDeathStats(filter));
        model.addAttribute("riskData", analysisService.getRiskStats(filter));
        model.addAttribute("syndromeConfig", syndromeConfigService.findByName(meta.syndromeType));
        model.addAttribute("models", warningService.getModelsBySyndrome(meta.syndromeType));

        Map<String, Object> warningFilter = new HashMap<>(filter);
        PageResult<WarningRecord> warningPageResult = warningService.searchPage(warningFilter, warningPage, 10);
        model.addAttribute("warnings", warningPageResult.getRecords());
        model.addAttribute("warningPage", warningPageResult);

        PageResult<CaseInfo> casePageResult = caseService.searchPage(filter, casePage, 15);
        model.addAttribute("cases", casePageResult.getRecords());
        model.addAttribute("casePage", casePageResult);

        return "syndrome/monitor";
    }

    private Map<String, Object> buildFilter(HttpSession session, String syndromeType, String district,
                                            String hospital, String startDate, String endDate, Integer days) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        return com.symptom.util.FilterViewHelper.buildScopedFilter(dataScopeService, user,
                syndromeType, district, hospital, startDate, endDate, days);
    }

    private String resolveCode(String uri) {
        if (uri == null) return "respiratory";
        String path = uri.startsWith("/") ? uri.substring(1) : uri;
        int slash = path.indexOf('/');
        if (slash > 0) {
            path = path.substring(0, slash);
        }
        return SYNDROMES.containsKey(path) ? path : "respiratory";
    }

    private static class SyndromeMeta {
        final String syndromeType;
        final String code;

        SyndromeMeta(String syndromeType, String code) {
            this.syndromeType = syndromeType;
            this.code = code;
        }
    }
}
