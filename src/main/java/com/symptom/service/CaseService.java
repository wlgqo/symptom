package com.symptom.service;

import com.symptom.common.PageResult;
import com.symptom.entity.*;
import com.symptom.mapper.*;
import com.symptom.util.QueryParamUtil;
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
        attachSymptoms(cases);
        return cases;
    }

    public PageResult<CaseInfo> searchPage(Map<String, Object> params, Integer page, Integer pageSize) {
        Map<String, Object> query = new HashMap<>(params);
        QueryParamUtil.applyPagination(query, page, pageSize);
        long total = caseInfoMapper.count(query);
        if (total == 0) {
            return PageResult.empty((Integer) query.get("page"), (Integer) query.get("pageSize"));
        }
        List<CaseInfo> records = caseInfoMapper.search(query);
        attachSymptoms(records);
        return new PageResult<>(records, total, (Integer) query.get("page"), (Integer) query.get("pageSize"));
    }

    private void attachSymptoms(List<CaseInfo> cases) {
        for (CaseInfo c : cases) {
            c.setSymptoms(caseSymptomMapper.findByCaseId(c.getId()));
        }
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
            log.setSnapshot(com.alibaba.fastjson.JSON.toJSONString(old));
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
        Map<String, Object> params = new HashMap<>();
        params.put("syndromeType", syndromeType);
        return search(params);
    }

    public List<CaseInfo> findSevereCases(String syndromeType) {
        return caseInfoMapper.findSevereCases(syndromeType);
    }

    public List<CaseInfo> findDeathCases(String syndromeType) {
        return caseInfoMapper.findDeathCases(syndromeType);
    }

    public List<CaseInfo> findByRiskLevel(String riskLevel) {
        List<CaseInfo> cases = caseInfoMapper.findByRiskLevel(riskLevel);
        attachSymptoms(cases);
        return cases;
    }

    public Map<String, Object> getDashboardStats(String syndromeType, String district, Integer days,
                                                 String startDate, String endDate) {
        return getDashboardStats(buildFilterParams(syndromeType, district, days, startDate, endDate));
    }

    public Map<String, Object> getDashboardStats(Map<String, Object> filter) {
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
        return stats;
    }

    public List<CaseInfo> searchLimited(Map<String, Object> params, int limit) {
        Map<String, Object> query = new HashMap<>(params);
        query.put("limit", limit);
        query.put("offset", 0);
        return caseInfoMapper.search(query);
    }

    public Map<String, Object> buildFilterParams(String syndromeType, String district, Integer days,
                                                  String startDate, String endDate) {
        return QueryParamUtil.baseFilter(syndromeType, district, startDate, endDate, days);
    }

    public int countBySyndromeType(String syndromeType) {
        return caseInfoMapper.countBySyndromeType(syndromeType);
    }

    public List<CaseInfo> searchByConditionTree(String conditionSql) {
        if (conditionSql == null || conditionSql.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<CaseInfo> cases = caseInfoMapper.searchByConditionTree(conditionSql);
        attachSymptoms(cases);
        return cases;
    }
}
