package com.symptom.mapper;

import com.symptom.entity.WarningRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WarningRecordMapper {
    List<WarningRecord> findAll();
    List<WarningRecord> findBySyndromeType(@Param("syndromeType") String syndromeType);
    WarningRecord findById(@Param("id") Integer id);
    int insert(WarningRecord record);
    int update(WarningRecord record);
    List<WarningRecord> findByStatus(@Param("status") String status);
    List<WarningRecord> findByModelId(@Param("modelId") Integer modelId);
    List<WarningRecord> search(@Param("syndromeType") String syndromeType, @Param("status") String status);
    int countPending();
}
