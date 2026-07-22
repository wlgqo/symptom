package com.symptom.controller;

import com.symptom.entity.CaseInfo;
import com.symptom.service.CaseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/diarrhea")
public class DiarrheaController {

    private static final String SYNDROME = "发热伴腹泻症候群";

    private final CaseService caseService;

    public DiarrheaController(CaseService caseService) {
        this.caseService = caseService;
    }

    @GetMapping
    public String index(Model model) {
        List<CaseInfo> allCases = caseService.findBySyndromeType(SYNDROME);
        List<CaseInfo> highRisk = caseService.findByRiskLevel("高风险");
        highRisk.removeIf(c -> !SYNDROME.equals(c.getSyndromeType()));

        model.addAttribute("syndromeType", SYNDROME);
        model.addAttribute("cases", allCases);
        model.addAttribute("highRiskCases", highRisk);
        return "diarrhea/index";
    }

    @GetMapping("/case/{id}")
    public String caseDetail(@PathVariable Integer id, Model model) {
        CaseInfo caseInfo = caseService.getById(id);
        model.addAttribute("caseInfo", caseInfo);
        return "diarrhea/detail";
    }
}
