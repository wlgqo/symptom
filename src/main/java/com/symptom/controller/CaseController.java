package com.symptom.controller;

import com.symptom.common.PageResult;
import com.symptom.entity.CaseInfo;
import com.symptom.entity.CaseModifyLog;
import com.symptom.entity.ReportCard;
import com.symptom.entity.SysUser;
import com.symptom.service.CaseService;
import com.symptom.service.DataScopeService;
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
    private final DataScopeService dataScopeService;

    public CaseController(CaseService caseService, DataScopeService dataScopeService) {
        this.caseService = caseService;
        this.dataScopeService = dataScopeService;
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
                       @RequestParam(required = false) String startDate,
                       @RequestParam(required = false) String endDate,
                       @RequestParam(required = false, defaultValue = "1") Integer page,
                       @RequestParam(required = false, defaultValue = "20") Integer pageSize,
                       HttpSession session,
                       Model model) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
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
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        dataScopeService.applyCaseScope(params, user);

        PageResult<CaseInfo> pageResult = caseService.searchPage(params, page, pageSize);
        model.addAttribute("cases", pageResult.getRecords());
        model.addAttribute("pageResult", pageResult);
        model.addAttribute("params", params);
        model.addAttribute("scopeDistrict", dataScopeService.scopeDistrict(user));
        model.addAttribute("pageTitle", "病例中心");
        model.addAttribute("breadcrumb", "主动监测病例");
        return "case/list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Integer id, Model model, HttpSession session) {
        CaseInfo caseInfo = caseService.getById(id);
        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (caseInfo != null && dataScopeService.hasDistrictScope(user)
                && !user.getDistrictScope().equals(caseInfo.getDistrict())) {
            return "redirect:/case/list";
        }
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
        CaseInfo caseInfo = caseService.getById(id);
        if (caseInfo != null && dataScopeService.hasDistrictScope(user)
                && !user.getDistrictScope().equals(caseInfo.getDistrict())) {
            return "redirect:/case/list";
        }
        model.addAttribute("caseInfo", caseInfo);
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
                       @RequestParam(required = false) String startDate,
                       @RequestParam(required = false) String endDate,
                       HttpSession session,
                       HttpServletResponse response) throws IOException {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        Map<String, Object> params = new HashMap<>();
        params.put("syndromeType", syndromeType);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        dataScopeService.applyCaseScope(params, user);
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
