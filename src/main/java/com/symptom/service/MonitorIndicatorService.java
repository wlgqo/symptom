package com.symptom.service;

import com.symptom.entity.MonitorIndicator;
import com.symptom.mapper.MonitorIndicatorMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MonitorIndicatorService {

    private final MonitorIndicatorMapper monitorIndicatorMapper;

    public MonitorIndicatorService(MonitorIndicatorMapper monitorIndicatorMapper) {
        this.monitorIndicatorMapper = monitorIndicatorMapper;
    }

    public List<MonitorIndicator> findAll() {
        return monitorIndicatorMapper.findAll();
    }

    public List<MonitorIndicator> findBySyndromeType(String syndromeType) {
        return monitorIndicatorMapper.findBySyndromeType(syndromeType);
    }

    public MonitorIndicator findById(Integer id) {
        return monitorIndicatorMapper.findById(id);
    }

    public void save(MonitorIndicator indicator) {
        if (indicator.getId() == null) {
            monitorIndicatorMapper.insert(indicator);
        } else {
            monitorIndicatorMapper.update(indicator);
        }
    }

    public void delete(Integer id) {
        monitorIndicatorMapper.delete(id);
    }
}
