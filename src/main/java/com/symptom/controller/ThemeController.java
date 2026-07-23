package com.symptom.controller;

import com.symptom.entity.CaseInfo;
import com.symptom.entity.SavedQuery;
import com.symptom.entity.SyndromeConfig;
import com.symptom.entity.SysUser;
import com.symptom.service.CaseService;
import com.symptom.service.SavedQueryService;
import com.symptom.service.SyndromeConfigService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/theme")
public class ThemeController {

    private final SyndromeConfigService syndromeConfigService;
    private final CaseService caseService;
    private final SavedQueryService savedQueryService;

    public ThemeController(SyndromeConfigService syndromeConfigService,
                           CaseService caseService,
                           SavedQueryService savedQueryService) {
        this.syndromeConfigService = syndromeConfigService;
        this.caseService = caseService;
        this.savedQueryService = savedQueryService;
    }

    @GetMapping
    public String index(@RequestParam(required = false) Integer id, Model model) {
        model.addAttribute("pageTitle", "症候群主题库");
        model.addAttribute("breadcrumb", "症候群主题库");
        List<SyndromeConfig> themes = syndromeConfigService.findAll();
        model.addAttribute("themes", themes);
        model.addAttribute("savedQueries", savedQueryService.findAll());

        SyndromeConfig selected = null;
        if (id != null) {
            selected = syndromeConfigService.findById(id);
        } else if (!themes.isEmpty()) {
            selected = themes.get(0);
        }
        model.addAttribute("selected", selected);
        if (selected != null) {
            int caseCount = caseService.countBySyndromeType(selected.getSyndromeName());
            model.addAttribute("caseCount", caseCount);
        }
        return "theme/index";
    }

    @PostMapping("/query/save")
    @ResponseBody
    public Map<String, Object> saveQuery(@RequestBody SavedQuery query, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        query.setCreatedBy(user.getRealName());
        savedQueryService.save(query);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return result;
    }

    @PostMapping("/preview")
    @ResponseBody
    public Map<String, Object> preview(@RequestBody Map<String, Object> request) {
        String conditionSql = buildConditionSql(request);
        List<CaseInfo> cases = caseService.searchByConditionTree(conditionSql);
        long highRisk = cases.stream().filter(c -> "高风险".equals(c.getRiskLevel())).count();
        long severe = cases.stream().filter(c -> c.getIsSevere() != null && c.getIsSevere() == 1).count();
        long death = cases.stream().filter(c -> c.getIsDeath() != null && c.getIsDeath() == 1).count();

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("count", cases.size());
        result.put("highRisk", highRisk);
        result.put("severe", severe);
        result.put("death", death);
        result.put("cases", cases.size() > 50 ? cases.subList(0, 50) : cases);
        return result;
    }

    @SuppressWarnings("unchecked")
    private String buildConditionSql(Map<String, Object> request) {
        List<Map<String, Object>> conditions = (List<Map<String, Object>>) request.get("conditions");
        String logic = (String) request.getOrDefault("logic", "AND");
        if (conditions == null || conditions.isEmpty()) {
            return "SELECT id FROM case_info";
        }
        StringBuilder where = new StringBuilder();
        for (int i = 0; i < conditions.size(); i++) {
            Map<String, Object> cond = conditions.get(i);
            String type = (String) cond.get("type");
            String operator = (String) cond.getOrDefault("operator", "=");
            String value = String.valueOf(cond.get("value"));
            if (i > 0) where.append(" ").append(logic).append(" ");
            switch (type) {
                case "symptom":
                    where.append("id IN (SELECT case_id FROM case_symptom WHERE symptom_name = '").append(value).append("')");
                    break;
                case "age":
                    where.append("age ").append(operator).append(" ").append(value);
                    break;
                case "gender":
                    where.append("gender = '").append(value).append("'");
                    break;
                case "district":
                    where.append("district LIKE '%").append(value).append("%'");
                    break;
                case "syndrome":
                    where.append("syndrome_type = '").append(value).append("'");
                    break;
                case "fever":
                    where.append("fever_temp ").append(operator).append(" ").append(value);
                    break;
                case "risk":
                    where.append("risk_level = '").append(value).append("'");
                    break;
                case "date":
                    where.append("report_date ").append(operator).append(" '").append(value).append("'");
                    break;
                case "diagnosis":
                    where.append("diagnosis LIKE '%").append(value).append("%'");
                    break;
                case "hospital":
                    where.append("hospital LIKE '%").append(value).append("%'");
                    break;
                default:
                    where.append("1=1");
            }
        }
        return "SELECT id FROM case_info WHERE " + where;
    }
}
