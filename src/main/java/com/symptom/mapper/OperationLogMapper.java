package com.symptom.mapper;

import com.symptom.entity.OperationLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OperationLogMapper {
    int insert(OperationLog log);
    List<OperationLog> findAll();
    List<OperationLog> findByUsername(@Param("username") String username);
}
