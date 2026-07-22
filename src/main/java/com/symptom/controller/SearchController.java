package com.symptom.controller;

import com.symptom.entity.CaseInfo;
import com.symptom.service.CaseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/search")
public class SearchController {

    private final CaseService caseService;

    public SearchController(CaseService caseService) {
        this.caseService = caseService;
    }

    @GetMapping
    public String index() {
        return "search/index";
    }

    @PostMapping("/execute")
    @ResponseBody
    public Map<String, Object> execute(@RequestBody Map<String, Object> request) {
        String conditionSql = buildConditionSql(request);
        List<CaseInfo> cases = caseService.searchByConditionTree(conditionSql);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("count", cases.size());
        result.put("cases", cases);
        result.put("sql", conditionSql);
        return result;
    }

    @SuppressWarnings("unchecked")
    private String buildConditionSql(Map<String, Object> request) {
        List<Map<String, Object>> conditions = (List<Map<String, Object>>) request.get("conditions");
        String logic = (String) request.getOrDefault("logic", "AND");

        if (conditions == null || conditions.isEmpty()) {
            return "SELECT id FROM case_info";
        }

        StringBuilder sql = new StringBuilder();
        for (int i = 0; i < conditions.size(); i++) {
            Map<String, Object> cond = conditions.get(i);
            String type = (String) cond.get("type");
            String operator = (String) cond.getOrDefault("operator", "=");
            String value = String.valueOf(cond.get("value"));

            if (i > 0) {
                sql.append(" ").append(logic).append(" ");
            }

            switch (type) {
                case "symptom":
                    sql.append("id IN (SELECT case_id FROM case_symptom WHERE symptom_name = '")
                       .append(value).append("')");
                    break;
                case "age":
                    sql.append("age ").append(operator).append(" ").append(value);
                    break;
                case "gender":
                    sql.append("gender = '").append(value).append("'");
                    break;
                case "district":
                    sql.append("district LIKE '%").append(value).append("%'");
                    break;
                case "syndrome":
                    sql.append("syndrome_type = '").append(value).append("'");
                    break;
                case "fever":
                    sql.append("fever_temp ").append(operator).append(" ").append(value);
                    break;
                case "risk":
                    sql.append("risk_level = '").append(value).append("'");
                    break;
                case "date":
                    sql.append("report_date ").append(operator).append(" '").append(value).append("'");
                    break;
                default:
                    sql.append("1=1");
            }
        }
        return sql.toString();
    }
}
