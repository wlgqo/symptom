package com.symptom.controller;

import com.symptom.entity.CaseInfo;
import com.symptom.service.CaseService;
import com.symptom.service.SyndromeConfigService;
import com.symptom.service.WarningService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/diarrhea")
public class DiarrheaController {

    private static final String SYNDROME = "发热伴腹泻症候群";

    private final CaseService caseService;
    private final SyndromeConfigService syndromeConfigService;
    private final WarningService warningService;

    public DiarrheaController(CaseService caseService, SyndromeConfigService syndromeConfigService,
                              WarningService warningService) {
        this.caseService = caseService;
        this.syndromeConfigService = syndromeConfigService;
        this.warningService = warningService;
    }

    @GetMapping
    public String index(Model model) {
        List<CaseInfo> allCases = caseService.findBySyndromeType(SYNDROME);
        List<CaseInfo> highRisk = caseService.findByRiskLevel("高风险");
        highRisk.removeIf(c -> !SYNDROME.equals(c.getSyndromeType()));
        long mediumRiskCount = allCases.stream().filter(c -> "中风险".equals(c.getRiskLevel())).count();

        model.addAttribute("syndromeType", SYNDROME);
        model.addAttribute("cases", allCases);
        model.addAttribute("highRiskCases", highRisk);
        model.addAttribute("mediumRiskCount", mediumRiskCount);
        model.addAttribute("syndromeConfig", syndromeConfigService.findByName(SYNDROME));
        model.addAttribute("models", warningService.getModelsBySyndrome(SYNDROME));
        return "diarrhea/index";
    }

    @GetMapping("/case/{id}")
    public String caseDetail(@PathVariable Integer id) {
        return "redirect:/case/detail/" + id;
    }
}
