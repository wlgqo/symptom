package com.symptom.controller;

import com.symptom.entity.CaseInfo;
import com.symptom.entity.SysUser;
import com.symptom.entity.WarningRecord;
import com.symptom.service.AnalysisService;
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
@RequestMapping("/respiratory")
public class RespiratoryController {

    private static final String SYNDROME = "发热呼吸道症候群";

    private final CaseService caseService;
    private final AnalysisService analysisService;
    private final WarningService warningService;

    public RespiratoryController(CaseService caseService, AnalysisService analysisService,
                                 WarningService warningService) {
        this.caseService = caseService;
        this.analysisService = analysisService;
        this.warningService = warningService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("syndromeType", SYNDROME);
        model.addAttribute("timeDataDay", analysisService.getTimeDistribution(SYNDROME, "day"));
        model.addAttribute("timeDataWeek", analysisService.getTimeDistribution(SYNDROME, "week"));
        model.addAttribute("timeDataMonth", analysisService.getTimeDistribution(SYNDROME, "month"));
        model.addAttribute("districtData", analysisService.getDistrictDistribution(SYNDROME));
        model.addAttribute("populationData", analysisService.getPopulationDistribution(SYNDROME));
        model.addAttribute("clinicalData", analysisService.getClinicalFeatures(SYNDROME));
        model.addAttribute("models", warningService.getModelsBySyndrome(SYNDROME));
        model.addAttribute("warnings", warningService.getRecordsBySyndrome(SYNDROME));
        model.addAttribute("cases", caseService.findBySyndromeType(SYNDROME));
        return "respiratory/index";
    }

    @PostMapping("/warning/run")
    @ResponseBody
    public Map<String, Object> runWarning(@RequestParam Integer modelId) {
        List<WarningRecord> records = warningService.runWarningAnalysis(SYNDROME, modelId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("count", records.size());
        result.put("records", records);
        return result;
    }

    @PostMapping("/warning/handle")
    @ResponseBody
    public Map<String, Object> handleWarning(@RequestParam Integer id, @RequestParam String result,
                                              HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        warningService.handleWarning(id, user.getRealName(), result);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return resp;
    }

    @GetMapping("/api/time")
    @ResponseBody
    public Map<String, Object> timeData(@RequestParam(defaultValue = "day") String groupBy) {
        return analysisService.getTimeDistribution(SYNDROME, groupBy);
    }
}
