package com.symptom.service;

import com.symptom.entity.WarningModel;
import com.symptom.entity.WarningRecord;
import com.symptom.mapper.CaseInfoMapper;
import com.symptom.mapper.WarningModelMapper;
import com.symptom.mapper.WarningRecordMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WarningService {

    private final WarningModelMapper modelMapper;
    private final WarningRecordMapper recordMapper;
    private final CaseInfoMapper caseInfoMapper;

    public WarningService(WarningModelMapper modelMapper, WarningRecordMapper recordMapper,
                          CaseInfoMapper caseInfoMapper) {
        this.modelMapper = modelMapper;
        this.recordMapper = recordMapper;
        this.caseInfoMapper = caseInfoMapper;
    }

    public List<WarningModel> getAllModels() {
        return modelMapper.findAll();
    }

    public List<WarningModel> getModelsBySyndrome(String syndromeType) {
        return modelMapper.findBySyndromeType(syndromeType);
    }

    public WarningModel getModelById(Integer id) {
        return modelMapper.findById(id);
    }

    public void updateModel(WarningModel model) {
        modelMapper.update(model);
    }

    public List<WarningRecord> getAllRecords() {
        return recordMapper.findAll();
    }

    public List<WarningRecord> getRecordsBySyndrome(String syndromeType) {
        return recordMapper.findBySyndromeType(syndromeType);
    }

    public WarningRecord getRecordById(Integer id) {
        return recordMapper.findById(id);
    }

    public void handleWarning(Integer id, String handler, String result) {
        WarningRecord record = recordMapper.findById(id);
        if (record != null) {
            record.setStatus("已处置");
            record.setHandler(handler);
            record.setHandleResult(result);
            recordMapper.update(record);
        }
    }

    public int countPending() {
        return recordMapper.countPending();
    }

    /**
     * 模拟预警模型计算，基于历史数据生成预警信号
     */
    public List<WarningRecord> runWarningAnalysis(String syndromeType, Integer modelId) {
        WarningModel model = modelMapper.findById(modelId);
        if (model == null) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> dailyData = caseInfoMapper.countByDate(syndromeType, "day");
        List<WarningRecord> newWarnings = new ArrayList<>();

        if (dailyData.isEmpty()) {
            return newWarnings;
        }

        double threshold = 3.0;
        String modelType = model.getModelType();

        switch (modelType) {
            case "固定阈值模型":
                threshold = 5.0;
                break;
            case "移动百分位模型":
                threshold = calculatePercentile(dailyData, 0.9);
                break;
            case "CUSUM累计和控制图模型":
                threshold = calculateMean(dailyData) * 1.5;
                break;
            case "移动流行区间模型":
                threshold = calculateMean(dailyData) * 1.3;
                break;
            case "EWMA指数加权移动平均模型":
                threshold = calculateEwma(dailyData) * 1.2;
                break;
            case "ARIMA模型":
                threshold = calculateMean(dailyData) * 1.4;
                break;
            case "场所聚集性模型":
                threshold = 3.0;
                break;
            default:
                threshold = 5.0;
        }

        Map<String, Object> latest = dailyData.get(dailyData.size() - 1);
        double latestCount = ((Number) latest.get("cnt")).doubleValue();

        if (latestCount > threshold) {
            WarningRecord record = new WarningRecord();
            record.setModelId(modelId);
            record.setSyndromeType(syndromeType);
            record.setWarningLevel(latestCount > threshold * 1.5 ? "红色" : "橙色");
            record.setWarningContent(String.format(
                "【%s】检测到异常：%s病例数%.0f例，超过阈值%.1f例",
                model.getModelName(), syndromeType, latestCount, threshold));
            record.setStatus("待处置");
            recordMapper.insert(record);
            newWarnings.add(record);
        }

        return newWarnings;
    }

    private double calculateMean(List<Map<String, Object>> data) {
        return data.stream()
                .mapToDouble(m -> ((Number) m.get("cnt")).doubleValue())
                .average().orElse(0);
    }

    private double calculatePercentile(List<Map<String, Object>> data, double percentile) {
        List<Double> values = new ArrayList<>();
        for (Map<String, Object> m : data) {
            values.add(((Number) m.get("cnt")).doubleValue());
        }
        Collections.sort(values);
        int index = (int) Math.ceil(percentile * values.size()) - 1;
        return values.get(Math.max(0, index));
    }

    private double calculateEwma(List<Map<String, Object>> data) {
        double alpha = 0.3;
        double ewma = ((Number) data.get(0).get("cnt")).doubleValue();
        for (int i = 1; i < data.size(); i++) {
            double val = ((Number) data.get(i).get("cnt")).doubleValue();
            ewma = alpha * val + (1 - alpha) * ewma;
        }
        return ewma;
    }
}
