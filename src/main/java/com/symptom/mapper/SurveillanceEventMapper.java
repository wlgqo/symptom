package com.symptom.mapper;

import com.symptom.entity.SurveillanceEvent;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SurveillanceEventMapper {
    List<SurveillanceEvent> findAll();
    SurveillanceEvent findById(@Param("id") Integer id);
    int insert(SurveillanceEvent event);
    int update(SurveillanceEvent event);
    int countByStatus(@Param("status") String status);
}
