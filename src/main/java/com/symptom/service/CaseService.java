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
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCases", caseInfoMapper.findAll().size());
        stats.put("todayCases", caseInfoMapper.countToday());
        stats.put("respiratoryCases", caseInfoMapper.countBySyndromeType("发热呼吸道症候群"));
        stats.put("hemorrhageCases", caseInfoMapper.countBySyndromeType("发热伴出血症候群"));
        stats.put("diarrheaCases", caseInfoMapper.countBySyndromeType("发热伴腹泻症候群"));
        stats.put("highRiskCases", caseInfoMapper.countHighRisk());
        return stats;
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
