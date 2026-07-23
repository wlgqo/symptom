package com.symptom.mapper;

import com.symptom.entity.SymptomTerm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SymptomTermMapper {
    List<SymptomTerm> findAll();

    SymptomTerm findById(@Param("id") Integer id);

    int insert(SymptomTerm term);

    int update(SymptomTerm term);

    int delete(@Param("id") Integer id);
}
