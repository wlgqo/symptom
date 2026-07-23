package com.symptom.mapper;

import com.symptom.entity.MonitorIndicator;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MonitorIndicatorMapper {
    List<MonitorIndicator> findAll();

    List<MonitorIndicator> findBySyndromeType(@Param("syndromeType") String syndromeType);

    MonitorIndicator findById(@Param("id") Integer id);

    int insert(MonitorIndicator indicator);

    int update(MonitorIndicator indicator);

    int delete(@Param("id") Integer id);
}
