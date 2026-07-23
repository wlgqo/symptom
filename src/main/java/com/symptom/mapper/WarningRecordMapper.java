package com.symptom.mapper;

import com.symptom.entity.WarningRecord;

import java.util.List;
import java.util.Map;

public interface WarningRecordMapper {
    List<WarningRecord> findAll();
    List<WarningRecord> findBySyndromeType(String syndromeType);
    WarningRecord findById(Integer id);
    int insert(WarningRecord record);
    int update(WarningRecord record);
    List<WarningRecord> findByStatus(String status);
    List<WarningRecord> findByModelId(Integer modelId);
    List<WarningRecord> search(Map<String, Object> params);
    int count(Map<String, Object> params);
    int countPending();
    int countScoped(Map<String, Object> params);
}
