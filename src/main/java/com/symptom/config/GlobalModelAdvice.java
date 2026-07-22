package com.symptom.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalModelAdvice {

    @ModelAttribute("activeMenu")
    public String activeMenu(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri.equals("/")) return "dashboard";
        if (uri.startsWith("/respiratory")) return "respiratory";
        if (uri.startsWith("/hemorrhage")) return "hemorrhage";
        if (uri.startsWith("/diarrhea")) return "diarrhea";
        if (uri.startsWith("/search")) return "search";
        if (uri.startsWith("/case")) return "case";
        if (uri.startsWith("/admin")) return "admin";
        return "";
    }
}
