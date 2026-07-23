package com.symptom.config;

import com.symptom.mapper.WarningRecordMapper;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@ControllerAdvice
public class GlobalModelAdvice {

    private final WarningRecordMapper warningRecordMapper;

    public GlobalModelAdvice(WarningRecordMapper warningRecordMapper) {
        this.warningRecordMapper = warningRecordMapper;
    }

    @ModelAttribute("activeMenu")
    public String activeMenu(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri.equals("/")) return "dashboard";
        if (uri.startsWith("/respiratory")) return "respiratory";
        if (uri.startsWith("/hemorrhage")) return "hemorrhage";
        if (uri.startsWith("/diarrhea")) return "diarrhea";
        if (uri.startsWith("/warning/model")) return "warning-model";
        if (uri.startsWith("/warning")) return "warning";
        if (uri.startsWith("/theme")) return "theme";
        if (uri.startsWith("/analysis")) return "analysis";
        if (uri.startsWith("/search")) return "analysis";
        if (uri.startsWith("/case")) return "case";
        if (uri.startsWith("/event")) return "event";
        if (uri.startsWith("/config")) return "config";
        if (uri.startsWith("/admin")) return "admin";
        return "";
    }

    @ModelAttribute
    public void pendingWarnings(HttpSession session) {
        session.setAttribute("pendingWarnings", warningRecordMapper.countPending());
    }
}
