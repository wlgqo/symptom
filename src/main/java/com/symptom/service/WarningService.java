package com.symptom.service;

import com.symptom.entity.*;
import com.symptom.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class WarningService {

    private final WarningModelMapper modelMapper;
    private final WarningRecordMapper recordMapper;
    private final WarningNotificationMapper notificationMapper;
    private final WarningDisposalMapper disposalMapper;
    private final CaseInfoMapper caseInfoMapper;

    public WarningService(WarningModelMapper modelMapper, WarningRecordMapper recordMapper,
                          WarningNotificationMapper notificationMapper,
                          WarningDisposalMapper disposalMapper,
                          CaseInfoMapper caseInfoMapper) {
        this.modelMapper = modelMapper;
        this.recordMapper = recordMapper;
        this.notificationMapper = notificationMapper;
        this.disposalMapper = disposalMapper;
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

    public List<WarningRecord> getRecordsByModelId(Integer modelId) {
        return recordMapper.findByModelId(modelId);
    }

    public WarningRecord getRecordById(Integer id) {
        return recordMapper.findById(id);
    }

    public List<WarningNotification> getNotifications(Integer warningId) {
        return notificationMapper.findByWarningId(warningId);
    }

    public List<WarningDisposal> getDisposals(Integer warningId) {
        return disposalMapper.findByWarningId(warningId);
    }

    public int countPending() {
        return recordMapper.countPending();
    }

    public Map<String, Object> getModelStats() {
        Map<String, Object> stats = new HashMap<>();
        List<WarningModel> models = modelMapper.findAll();
        stats.put("total", models.size());
        stats.put("enabled", models.stream().filter(m -> m.getEnabled() != null && m.getEnabled() == 1).count());
        stats.put("triggered", recordMapper.findAll().size());
        return stats;
    }

    @Transactional
    public void processAction(Integer warningId, String action, String operator, String comment) {
        WarningRecord record = recordMapper.findById(warningId);
        if (record == null) return;

        String newStatus;
        switch (action) {
            case "confirm":
                newStatus = "已确认";
                break;
            case "dispose":
                newStatus = "处置中";
                break;
            case "complete":
                newStatus = "已完成";
                break;
            case "close":
                newStatus = "已关闭";
                break;
            default:
                newStatus = record.getStatus();
        }

        record.setStatus(newStatus);
        record.setHandler(operator);
        record.setHandleResult(comment);
        recordMapper.update(record);

        WarningDisposal disposal = new WarningDisposal();
        disposal.setWarningId(warningId);
        disposal.setOperator(operator);
        disposal.setActionType(actionLabel(action));
        disposal.setActionComment(comment);
        disposalMapper.insert(disposal);
    }

    public void sendNotification(Integer warningId, String target, String method) {
        WarningNotification notification = new WarningNotification();
        notification.setWarningId(warningId);
        notification.setNotifyTarget(target);
        notification.setNotifyMethod(method);
        notification.setNotifyStatus("已发送");
        notificationMapper.insert(notification);
    }

    public void handleWarning(Integer id, String handler, String result) {
        processAction(id, "complete", handler, result);
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
        double baseline = calculateMean(dailyData);
        String modelType = model.getModelType();

        switch (modelType) {
            case "固定阈值模型":
                threshold = 5.0;
                break;
            case "移动百分位模型":
                threshold = calculatePercentile(dailyData, 0.9);
                break;
            case "CUSUM累计和控制图模型":
                threshold = baseline * 1.5;
                break;
            case "移动流行区间模型":
                threshold = baseline * 1.3;
                break;
            case "EWMA指数加权移动平均模型":
                threshold = calculateEwma(dailyData) * 1.2;
                break;
            case "ARIMA模型":
                threshold = baseline * 1.4;
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
            record.setStatus("待研判");
            record.setObservedValue(latestCount);
            record.setBaselineValue(baseline);
            record.setThresholdValue(threshold);
            record.setAnomalyDegree(latestCount > threshold * 1.5 ? "严重" : "中等");
            record.setAnomalyType("异常增长");
            record.setDistrict("朝阳区");
            record.setHospital("市第三人民医院");
            recordMapper.insert(record);
            newWarnings.add(record);

            sendNotification(record.getId(), "疾控业务人员", "站内消息");
            sendNotification(record.getId(), "监测分析人员", "短信");
        }

        return newWarnings;
    }

    private String actionLabel(String action) {
        switch (action) {
            case "confirm": return "确认异常";
            case "dispose": return "启动处置";
            case "complete": return "完成处置";
            case "close": return "关闭预警";
            default: return action;
        }
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
