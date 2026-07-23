package com.symptom.service;

import com.alibaba.fastjson.JSON;
import com.symptom.entity.*;
import com.symptom.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CaseService {

    private final CaseInfoMapper caseInfoMapper;
    private final CaseSymptomMapper caseSymptomMapper;
    private final CaseModifyLogMapper modifyLogMapper;
    private final ReportCardMapper reportCardMapper;

    public CaseService(CaseInfoMapper caseInfoMapper, CaseSymptomMapper caseSymptomMapper,
                       CaseModifyLogMapper modifyLogMapper, ReportCardMapper reportCardMapper) {
        this.caseInfoMapper = caseInfoMapper;
        this.caseSymptomMapper = caseSymptomMapper;
        this.modifyLogMapper = modifyLogMapper;
        this.reportCardMapper = reportCardMapper;
    }

    public List<CaseInfo> search(Map<String, Object> params) {
        List<CaseInfo> cases = caseInfoMapper.search(params);
        for (CaseInfo c : cases) {
            c.setSymptoms(caseSymptomMapper.findByCaseId(c.getId()));
        }
        return cases;
    }

    public CaseInfo getById(Integer id) {
        CaseInfo c = caseInfoMapper.findById(id);
        if (c != null) {
            c.setSymptoms(caseSymptomMapper.findByCaseId(id));
        }
        return c;
    }

    @Transactional
    public void update(CaseInfo caseInfo, String operator) {
        CaseInfo old = caseInfoMapper.findById(caseInfo.getId());
        if (old != null) {
            CaseModifyLog log = new CaseModifyLog();
            log.setCaseId(caseInfo.getId());
            log.setOperator(operator);
            log.setSnapshot(JSON.toJSONString(old));
            log.setChangeDesc("修改病例信息");
            modifyLogMapper.insert(log);
        }
        caseInfoMapper.update(caseInfo);
    }

    public List<CaseModifyLog> getModifyLogs(Integer caseId) {
        return modifyLogMapper.findByCaseId(caseId);
    }

    public List<ReportCard> getReportCards(Integer caseId) {
        return reportCardMapper.findByCaseId(caseId);
    }

    public List<CaseInfo> findBySyndromeType(String syndromeType) {
        List<CaseInfo> cases = caseInfoMapper.findBySyndromeType(syndromeType);
        for (CaseInfo c : cases) {
            c.setSymptoms(caseSymptomMapper.findByCaseId(c.getId()));
        }
        return cases;
    }

    public List<CaseInfo> findSevereCases(String syndromeType) {
        return caseInfoMapper.findSevereCases(syndromeType);
    }

    public List<CaseInfo> findDeathCases(String syndromeType) {
        return caseInfoMapper.findDeathCases(syndromeType);
    }

    public List<CaseInfo> findByRiskLevel(String riskLevel) {
        List<CaseInfo> cases = caseInfoMapper.findByRiskLevel(riskLevel);
        for (CaseInfo c : cases) {
            c.setSymptoms(caseSymptomMapper.findByCaseId(c.getId()));
        }
        return cases;
    }

    public Map<String, Object> getDashboardStats() {
        return getDashboardStats(null, null, null);
    }

    public Map<String, Object> getDashboardStats(String syndromeType, String district, Integer days) {
        Map<String, Object> filter = buildFilterParams(syndromeType, district, days);
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCases", caseInfoMapper.count(filter));
        stats.put("todayCases", caseInfoMapper.countToday());
        Map<String, Object> highRiskFilter = new HashMap<>(filter);
        highRiskFilter.put("riskLevel", "高风险");
        stats.put("highRiskCases", caseInfoMapper.count(highRiskFilter));
        Map<String, Object> severeFilter = new HashMap<>(filter);
        severeFilter.put("isSevere", 1);
        stats.put("severeCases", caseInfoMapper.count(severeFilter));
        Map<String, Object> deathFilter = new HashMap<>(filter);
        deathFilter.put("isDeath", 1);
        stats.put("deathCases", caseInfoMapper.count(deathFilter));
        stats.put("respiratoryCases", caseInfoMapper.countBySyndromeType("发热呼吸道症候群"));
        stats.put("hemorrhageCases", caseInfoMapper.countBySyndromeType("发热伴出血症候群"));
        stats.put("diarrheaCases", caseInfoMapper.countBySyndromeType("发热伴腹泻症候群"));
        return stats;
    }

    public List<CaseInfo> searchLimited(Map<String, Object> params, int limit) {
        List<CaseInfo> cases = search(params);
        if (cases.size() > limit) {
            return cases.subList(0, limit);
        }
        return cases;
    }

    private Map<String, Object> buildFilterParams(String syndromeType, String district, Integer days) {
        Map<String, Object> params = new HashMap<>();
        if (syndromeType != null && !syndromeType.isEmpty()) {
            params.put("syndromeType", syndromeType);
        }
        if (district != null && !district.isEmpty()) {
            params.put("district", district);
        }
        if (days != null && days > 0) {
            params.put("days", days);
        }
        return params;
    }

    public Map<String, Object> buildFilterParamsPublic(String syndromeType, String district, Integer days) {
        return buildFilterParams(syndromeType, district, days);
    }

    public int countBySyndromeType(String syndromeType) {
        return caseInfoMapper.countBySyndromeType(syndromeType);
    }

    public List<CaseInfo> searchByConditionTree(String conditionSql) {
        if (conditionSql == null || conditionSql.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<CaseInfo> cases = caseInfoMapper.searchByConditionTree(conditionSql);
        for (CaseInfo c : cases) {
            c.setSymptoms(caseSymptomMapper.findByCaseId(c.getId()));
        }
        return cases;
    }
}
