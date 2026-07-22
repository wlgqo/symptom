package com.symptom.mapper;

import com.symptom.entity.WarningModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WarningModelMapper {
    List<WarningModel> findAll();
    WarningModel findById(@Param("id") Integer id);
    List<WarningModel> findBySyndromeType(@Param("syndromeType") String syndromeType);
    int update(WarningModel model);
}
