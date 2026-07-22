package com.symptom.controller;

import com.symptom.entity.CaseInfo;
import com.symptom.service.AnalysisService;
import com.symptom.service.CaseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/hemorrhage")
public class HemorrhageController {

    private static final String SYNDROME = "发热伴出血症候群";

    private final CaseService caseService;
    private final AnalysisService analysisService;

    public HemorrhageController(CaseService caseService, AnalysisService analysisService) {
        this.caseService = caseService;
        this.analysisService = analysisService;
    }

    @GetMapping
    public String index(Model model) {
        Map<String, Object> stats = analysisService.getSevereDeathStats(SYNDROME);
        List<CaseInfo> severeCases = caseService.findSevereCases(SYNDROME);
        List<CaseInfo> deathCases = caseService.findDeathCases(SYNDROME);

        model.addAttribute("syndromeType", SYNDROME);
        model.addAttribute("stats", stats);
        model.addAttribute("severeCases", severeCases);
        model.addAttribute("deathCases", deathCases);
        return "hemorrhage/index";
    }

    @GetMapping("/case/{id}")
    public String caseDetail(@PathVariable Integer id, Model model) {
        CaseInfo caseInfo = caseService.getById(id);
        model.addAttribute("caseInfo", caseInfo);
        return "hemorrhage/detail";
    }
}
