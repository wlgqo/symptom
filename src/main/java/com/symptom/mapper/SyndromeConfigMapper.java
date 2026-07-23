package com.symptom.mapper;

import com.symptom.entity.SyndromeConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SyndromeConfigMapper {
    List<SyndromeConfig> findAll();
    SyndromeConfig findByName(String syndromeName);
}
