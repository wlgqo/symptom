package com.symptom.mapper;

import com.symptom.entity.WarningDisposal;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WarningDisposalMapper {
    List<WarningDisposal> findByWarningId(@Param("warningId") Integer warningId);
    int insert(WarningDisposal disposal);
}
