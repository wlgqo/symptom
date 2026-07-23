package com.symptom.controller;

import com.symptom.entity.CaseInfo;
import com.symptom.entity.CaseModifyLog;
import com.symptom.entity.ReportCard;
import com.symptom.entity.SysUser;
import com.symptom.service.CaseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/case")
public class CaseController {

    private final CaseService caseService;

    public CaseController(CaseService caseService) {
        this.caseService = caseService;
    }

    @GetMapping("/list")
    public String list(@RequestParam(required = false) String patientName,
                       @RequestParam(required = false) String mainIndex,
                       @RequestParam(required = false) String gender,
                       @RequestParam(required = false) String district,
                       @RequestParam(required = false) String caseType,
                       @RequestParam(required = false) String syndromeType,
                       @RequestParam(required = false) Integer ageMin,
                       @RequestParam(required = false) Integer ageMax,
                       @RequestParam(required = false) String discoverType,
                       Model model) {
        Map<String, Object> params = new HashMap<>();
        params.put("patientName", patientName);
        params.put("mainIndex", mainIndex);
        params.put("gender", gender);
        params.put("district", district);
        params.put("caseType", caseType);
        params.put("syndromeType", syndromeType);
        params.put("ageMin", ageMin);
        params.put("ageMax", ageMax);
        params.put("discoverType", discoverType);

        List<CaseInfo> cases = caseService.search(params);
        model.addAttribute("cases", cases);
        model.addAttribute("params", params);
        model.addAttribute("pageTitle", "病例中心");
        model.addAttribute("breadcrumb", "主动监测病例");
        return "case/list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        CaseInfo caseInfo = caseService.getById(id);
        List<CaseModifyLog> logs = caseService.getModifyLogs(id);
        List<ReportCard> cards = caseService.getReportCards(id);
        model.addAttribute("caseInfo", caseInfo);
        model.addAttribute("logs", logs);
        model.addAttribute("cards", cards);
        model.addAttribute("pageTitle", "病例360°画像");
        model.addAttribute("breadcrumb", "病例详情");
        return "case/detail";
    }

    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable Integer id, Model model, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        if ("浏览人员".equals(user.getRole())) {
            return "redirect:/case/detail/" + id;
        }
        model.addAttribute("caseInfo", caseService.getById(id));
        return "case/edit";
    }

    @PostMapping("/update")
    public String update(CaseInfo caseInfo, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        caseService.update(caseInfo, user.getRealName());
        return "redirect:/case/detail/" + caseInfo.getId();
    }

    @GetMapping("/export")
    public void export(@RequestParam(required = false) String syndromeType,
                       HttpServletResponse response) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("syndromeType", syndromeType);
        List<CaseInfo> cases = caseService.search(params);

        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=cases_export.csv");
        response.getOutputStream().write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});

        PrintWriter writer = response.getWriter();
        writer.println("主索引号,姓名,性别,年龄,病例类型,症候群类型,地区,诊断,风险等级,报告日期");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (CaseInfo c : cases) {
            writer.printf("%s,%s,%s,%d,%s,%s,%s,%s,%s,%s%n",
                    c.getMainIndex(), c.getPatientName(), c.getGender(), c.getAge(),
                    c.getCaseType(), c.getSyndromeType(), c.getDistrict(),
                    c.getDiagnosis(), c.getRiskLevel(),
                    c.getReportDate() != null ? sdf.format(c.getReportDate()) : "");
        }
        writer.flush();
    }
}
