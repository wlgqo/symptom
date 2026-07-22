package com.symptom.mapper;

import com.symptom.entity.ReportCard;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ReportCardMapper {
    List<ReportCard> findByCaseId(@Param("caseId") Integer caseId);
}
