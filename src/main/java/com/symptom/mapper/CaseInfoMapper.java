package com.symptom.mapper;

import com.symptom.entity.CaseInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CaseInfoMapper {
    List<CaseInfo> findAll();
    CaseInfo findById(@Param("id") Integer id);
    List<CaseInfo> search(Map<String, Object> params);
    int count(Map<String, Object> params);
    int insert(CaseInfo caseInfo);
    int update(CaseInfo caseInfo);
    List<CaseInfo> findBySyndromeType(@Param("syndromeType") String syndromeType);
    List<CaseInfo> findSevereCases(@Param("syndromeType") String syndromeType);
    List<CaseInfo> findDeathCases(@Param("syndromeType") String syndromeType);
    List<CaseInfo> findByRiskLevel(@Param("riskLevel") String riskLevel);
    List<Map<String, Object>> countByDate(Map<String, Object> params);
    List<Map<String, Object>> countByDistrict(Map<String, Object> params);
    List<Map<String, Object>> countByAgeGroup(Map<String, Object> params);
    List<Map<String, Object>> countByGender(Map<String, Object> params);
    List<Map<String, Object>> countByOccupation(Map<String, Object> params);
    int countToday();
    int countHighRisk();
    int countBySyndromeType(@Param("syndromeType") String syndromeType);
    int countSevere();
    int countDeath();
    List<CaseInfo> searchByConditionTree(@Param("sql") String sql);
}
