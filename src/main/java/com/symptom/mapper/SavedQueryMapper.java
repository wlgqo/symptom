package com.symptom.mapper;

import com.symptom.entity.SavedQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SavedQueryMapper {
    List<SavedQuery> findAll();
    SavedQuery findById(@Param("id") Integer id);
    int insert(SavedQuery query);
    int delete(@Param("id") Integer id);
}
