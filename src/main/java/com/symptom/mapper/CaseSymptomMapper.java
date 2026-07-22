package com.symptom.mapper;

import com.symptom.entity.CaseSymptom;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CaseSymptomMapper {
    List<String> findByCaseId(@Param("caseId") Integer caseId);
    List<CaseSymptom> findAllByCaseId(@Param("caseId") Integer caseId);
    int insert(@Param("caseId") Integer caseId, @Param("symptomName") String symptomName);
    int deleteByCaseId(@Param("caseId") Integer caseId);
}
