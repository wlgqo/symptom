package com.symptom.controller;

import com.symptom.common.PageResult;
import com.symptom.entity.CaseInfo;
import com.symptom.entity.CaseModifyLog;
import com.symptom.entity.ReportCard;
import com.symptom.entity.SysUser;
import com.symptom.service.CaseService;
import com.symptom.service.DataScopeService;
import com.symptom.service.FilterOptionService;
import com.symptom.util.CaseMedicalRecordBuilder;
import com.symptom.util.SensitiveDataUtil;
import com.symptom.util.FilterViewHelper;
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
    private final FilterOptionService filterOptionService;

    public CaseController(CaseService caseService, DataScopeService dataScopeService,
                            FilterOptionService filterOptionService) {
        this.caseService = caseService;
        this.dataScopeService = dataScopeService;
        this.filterOptionService = filterOptionService;
    }

    @GetMapping("/list")
    public String list(@RequestParam(required = false) String patientName,
                       @RequestParam(required = false) String mainIndex,
                       @RequestParam(required = false) String gender,
                       @RequestParam(required = false) String district,
                       @RequestParam(required = false) String hospital,
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
        String resolvedDistrict = dataScopeService.resolveDistrict(district, user);
        String resolvedHospital = dataScopeService.resolveHospital(hospital, user);
        Map<String, Object> params = new HashMap<>();
        params.put("patientName", patientName);
        params.put("mainIndex", mainIndex);
        params.put("gender", gender);
        params.put("caseType", caseType);
        params.put("syndromeType", syndromeType);
        params.put("ageMin", ageMin);
        params.put("ageMax", ageMax);
        params.put("discoverType", discoverType);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        dataScopeService.putResolvedFilters(params, user, district, hospital);

        PageResult<CaseInfo> pageResult = caseService.searchPage(params, page, pageSize);
        model.addAttribute("cases", pageResult.getRecords());
        model.addAttribute("pageResult", pageResult);
        params.put("district", resolvedDistrict);
        params.put("hospital", resolvedHospital);
        model.addAttribute("params", params);
        FilterViewHelper.addRegionHospitalModel(model, filterOptionService, dataScopeService, user, district, hospital);
        model.addAttribute("pageTitle", "病例中心");
        model.addAttribute("breadcrumb", "主动监测病例");
        return "case/list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Integer id, Model model, HttpSession session) {
        CaseInfo caseInfo = caseService.getById(id);
        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (caseInfo != null && !canAccessCase(user, caseInfo)) {
            return "redirect:/case/list";
        }
        List<CaseModifyLog> logs = caseService.getModifyLogs(id);
        List<ReportCard> cards = caseService.getReportCards(id);
        model.addAttribute("caseInfo", caseInfo);
        if (caseInfo != null) {
            model.addAttribute("displayProfileJson", CaseMedicalRecordBuilder.effectiveProfileJson(caseInfo));
            model.addAttribute("displayMedicalJson", CaseMedicalRecordBuilder.effectiveMedicalRecordJson(caseInfo));
        }
        model.addAttribute("logs", logs);
        model.addAttribute("cards", cards);
        model.addAttribute("pageTitle", "病例360°画像");
        model.addAttribute("breadcrumb", "病例详情");
        return "case/detail";
    }

    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable Integer id) {
        return "redirect:/case/detail/" + id;
    }

    @PostMapping("/update")
    public String update(@RequestParam Integer id) {
        return "redirect:/case/detail/" + id;
    }

    @GetMapping("/export")
    public void export(@RequestParam(required = false) String syndromeType,
                       @RequestParam(required = false) String district,
                       @RequestParam(required = false) String hospital,
                       @RequestParam(required = false) String startDate,
                       @RequestParam(required = false) String endDate,
                       HttpSession session,
                       HttpServletResponse response) throws IOException {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        Map<String, Object> params = FilterViewHelper.buildScopedFilter(dataScopeService, user,
                syndromeType, district, hospital, startDate, endDate, null);
        List<CaseInfo> cases = caseService.search(params);

        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=cases_export.csv");
        response.getOutputStream().write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});

        PrintWriter writer = response.getWriter();
        writer.println("主索引号,姓名,性别,年龄,证件号,手机号,病例类型,症候群类型,地区,诊断,风险等级,报告日期");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (CaseInfo c : cases) {
            writer.printf("%s,%s,%s,%d,%s,%s,%s,%s,%s,%s,%s,%s%n",
                    c.getMainIndex(), c.getPatientName(), c.getGender(), c.getAge(),
                    SensitiveDataUtil.maskIdCard(c.getIdCard()), SensitiveDataUtil.maskPhone(c.getPhone()),
                    c.getCaseType(), c.getSyndromeType(), c.getDistrict(),
                    c.getDiagnosis(), c.getRiskLevel(),
                    c.getReportDate() != null ? sdf.format(c.getReportDate()) : "");
        }
        writer.flush();
    }

    private boolean canAccessCase(SysUser user, CaseInfo caseInfo) {
        if (dataScopeService.isAdmin(user) || dataScopeService.isProvincial(user)) {
            return true;
        }
        if (dataScopeService.isCityWide(user)) {
            return caseInfo.getDistrict() != null
                    && FilterOptionService.getChengduDistricts().contains(caseInfo.getDistrict());
        }
        if (dataScopeService.hasDistrictScope(user)
                && !user.getDistrictScope().equals(caseInfo.getDistrict())) {
            return false;
        }
        if (dataScopeService.hasHospitalScope(user)
                && caseInfo.getHospital() != null
                && !user.getHospitalScope().equals(caseInfo.getHospital())) {
            return false;
        }
        return true;
    }
}
