package com.symptom.mapper;

import com.symptom.entity.CaseModifyLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CaseModifyLogMapper {
    List<CaseModifyLog> findByCaseId(@Param("caseId") Integer caseId);
    int insert(CaseModifyLog log);
}
