package com.symptom.config;

import com.symptom.entity.SysUser;
import com.symptom.mapper.WarningRecordMapper;
import com.symptom.service.DataScopeService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalModelAdvice {

    private final WarningRecordMapper warningRecordMapper;
    private final DataScopeService dataScopeService;

    public GlobalModelAdvice(WarningRecordMapper warningRecordMapper,
                             DataScopeService dataScopeService) {
        this.warningRecordMapper = warningRecordMapper;
        this.dataScopeService = dataScopeService;
    }

    @ModelAttribute("activeMenu")
    public String activeMenu(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri.equals("/")) return "dashboard";
        if (uri.startsWith("/respiratory")) return "respiratory";
        if (uri.startsWith("/hemorrhage")) return "hemorrhage";
        if (uri.startsWith("/diarrhea")) return "diarrhea";
        if (uri.startsWith("/rash")) return "rash";
        if (uri.startsWith("/encephalitis")) return "encephalitis";
        if (uri.startsWith("/fuo")) return "fuo";
        if (uri.startsWith("/warning/model")) return "warning-model";
        if (uri.startsWith("/warning")) return "warning";
        if (uri.startsWith("/theme")) return "theme";
        if (uri.startsWith("/analysis")) return "analysis";
        if (uri.startsWith("/search")) return "analysis";
        if (uri.startsWith("/case")) return "case";
        if (uri.startsWith("/event")) return "event";
        if (uri.startsWith("/config")) return "syndrome-config";
        if (uri.startsWith("/admin")) return "admin";
        return "";
    }

    @ModelAttribute
    public void pendingWarnings(HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        Map<String, Object> scope = new HashMap<>();
        if (user != null) {
            dataScopeService.applyWarningScope(scope, user);
        }
        int count = scope.isEmpty()
                ? warningRecordMapper.countPending()
                : warningRecordMapper.countScoped(scope);
        session.setAttribute("pendingWarnings", count);
    }
}
