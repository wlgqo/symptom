package com.symptom.controller;

import com.symptom.entity.SurveillanceEvent;
import com.symptom.entity.SysUser;
import com.symptom.service.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/event")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public String index(@RequestParam(required = false) Integer id, Model model) {
        model.addAttribute("pageTitle", "事件中心");
        model.addAttribute("breadcrumb", "事件中心");
        List<SurveillanceEvent> events = eventService.findAll();
        model.addAttribute("events", events);

        SurveillanceEvent selected = null;
        if (id != null) {
            selected = eventService.findById(id);
        } else if (!events.isEmpty()) {
            selected = events.get(0);
        }
        model.addAttribute("selected", selected);
        return "event/index";
    }

    @PostMapping("/update")
    @ResponseBody
    public Map<String, Object> update(@RequestBody Map<String, Object> body, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        Map<String, Object> result = new HashMap<>();
        Integer id = (Integer) body.get("id");
        String status = (String) body.get("status");
        String description = (String) body.getOrDefault("description", "");
        eventService.updateStatus(id, status, user.getRealName(), description);
        result.put("success", true);
        return result;
    }
}
