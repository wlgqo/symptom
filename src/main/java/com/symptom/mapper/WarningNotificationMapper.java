package com.symptom.mapper;

import com.symptom.entity.WarningNotification;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WarningNotificationMapper {
    List<WarningNotification> findByWarningId(@Param("warningId") Integer warningId);
    int insert(WarningNotification notification);
}
