package com.symptom.controller;

import com.symptom.entity.CaseInfo;
import com.symptom.service.AnalysisService;
import com.symptom.service.CaseService;
import com.symptom.service.SyndromeConfigService;
import com.symptom.service.WarningService;
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
    private final SyndromeConfigService syndromeConfigService;
    private final WarningService warningService;

    public HemorrhageController(CaseService caseService, AnalysisService analysisService,
                                SyndromeConfigService syndromeConfigService,
                                WarningService warningService) {
        this.caseService = caseService;
        this.analysisService = analysisService;
        this.syndromeConfigService = syndromeConfigService;
        this.warningService = warningService;
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
        model.addAttribute("syndromeConfig", syndromeConfigService.findByName(SYNDROME));
        model.addAttribute("models", warningService.getModelsBySyndrome(SYNDROME));
        return "hemorrhage/index";
    }

    @GetMapping("/case/{id}")
    public String caseDetail(@PathVariable Integer id) {
        return "redirect:/case/detail/" + id;
    }
}
