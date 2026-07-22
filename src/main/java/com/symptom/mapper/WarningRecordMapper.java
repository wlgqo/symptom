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
    int countPending();
}
