package com.symptom.service;

import com.symptom.entity.SurveillanceEvent;
import com.symptom.mapper.SurveillanceEventMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    private final SurveillanceEventMapper eventMapper;

    public EventService(SurveillanceEventMapper eventMapper) {
        this.eventMapper = eventMapper;
    }

    public List<SurveillanceEvent> findAll() {
        return eventMapper.findAll();
    }

    public SurveillanceEvent findById(Integer id) {
        return eventMapper.findById(id);
    }

    public void updateStatus(Integer id, String status, String responsiblePerson, String description) {
        SurveillanceEvent event = eventMapper.findById(id);
        if (event != null) {
            event.setStatus(status);
            if (responsiblePerson != null) {
                event.setResponsiblePerson(responsiblePerson);
            }
            if (description != null) {
                event.setDescription(description);
            }
            eventMapper.update(event);
        }
    }

    public int countPending() {
        return eventMapper.countByStatus("待核查");
    }
}
