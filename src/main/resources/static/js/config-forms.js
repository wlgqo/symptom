/** 业务配置表单：页面用普通字段，保存时组装为 JSON */

function splitList(str) {
    if (!str || !str.trim()) return [];
    return str.split(/[,，、\n]/).map(function(s) { return s.trim(); }).filter(Boolean);
}

function joinList(arr) {
    return (arr || []).join('、');
}

function parseSyndromeRules(json) {
    var data = {};
    try { data = json ? JSON.parse(json) : {}; } catch (e) { data = {}; }
    var symptoms = data.symptoms || {};
    return {
        logic: data.logic || 'AND',
        requiredSymptoms: joinList(symptoms.required),
        anySymptoms: joinList(symptoms.anyOf),
        anySigns: joinList((data.signs || {}).anyOf),
        anyLab: joinList((data.lab || {}).anyOf),
        exclude: joinList(data.exclude)
    };
}

function buildSyndromeRules(form) {
    var rules = {
        logic: form.logic.value,
        symptoms: {
            required: splitList(form.requiredSymptoms.value),
            anyOf: splitList(form.anySymptoms.value)
        }
    };
    var signs = splitList(form.anySigns.value);
    if (signs.length) rules.signs = { anyOf: signs };
    var lab = splitList(form.anyLab.value);
    if (lab.length) rules.lab = { anyOf: lab };
    var exclude = splitList(form.exclude.value);
    if (exclude.length) rules.exclude = exclude;
    return JSON.stringify(rules);
}

function parseRiskRules(json) {
    var data = {};
    try { data = json ? JSON.parse(json) : {}; } catch (e) { data = {}; }
    return {
        highRisk: joinList(data.highRisk),
        mediumRisk: joinList(data.mediumRisk)
    };
}

function buildRiskRules(form) {
    return JSON.stringify({
        highRisk: splitList(form.highRisk.value),
        mediumRisk: splitList(form.mediumRisk.value)
    });
}

function parseMonitorModel(json) {
    var data = {};
    try { data = json ? JSON.parse(json) : {}; } catch (e) { data = {}; }
    return {
        models: joinList(data.models),
        indicators: joinList(data.indicators)
    };
}

function buildMonitorModel(form) {
    return JSON.stringify({
        models: splitList(form.monitorModels.value),
        indicators: splitList(form.monitorIndicators.value)
    });
}

function parseLevelThresholds(json) {
    var levels = { low: {}, mid: {}, high: {} };
    try {
        var data = json ? JSON.parse(json) : {};
        (data.levels || []).forEach(function(lv) {
            var name = String(lv.level || '');
            var target = name.indexOf('低') >= 0 ? 'low' : (name.indexOf('中') >= 0 ? 'mid' : (name.indexOf('高') >= 0 ? 'high' : null));
            if (!target) return;
            levels[target] = {
                caseCount: lv.caseCount != null ? lv.caseCount : '',
                risePercent: lv.risePercent != null ? lv.risePercent : '',
                color: lv.color || ''
            };
        });
    } catch (e) { /* ignore */ }
    return levels;
}

function buildLevelThresholds(prefix) {
    function num(id) {
        var v = document.getElementById(prefix + id).value;
        return v === '' ? 0 : parseFloat(v);
    }
    return JSON.stringify({
        levels: [
            { level: '低风险', caseCount: num('LowCase'), risePercent: num('LowRise'), color: '#52c41a' },
            { level: '中风险', caseCount: num('MidCase'), risePercent: num('MidRise'), color: '#faad14' },
            { level: '高风险', caseCount: num('HighCase'), risePercent: num('HighRise'), color: '#ff4d4f' }
        ]
    });
}

function fillLevelThresholds(prefix, json) {
    var levels = parseLevelThresholds(json);
    ['low', 'mid', 'high'].forEach(function(key) {
        var cap = key.charAt(0).toUpperCase() + key.slice(1);
        var lv = levels[key] || {};
        var caseEl = document.getElementById(prefix + cap + 'Case');
        var riseEl = document.getElementById(prefix + cap + 'Rise');
        if (caseEl) caseEl.value = lv.caseCount !== '' ? lv.caseCount : '';
        if (riseEl) riseEl.value = lv.risePercent !== '' ? lv.risePercent : '';
    });
}

function parseModelConfig(json) {
    var data = {};
    try { data = json ? JSON.parse(json) : {}; } catch (e) { data = {}; }
    return data;
}

function renderModelConfigFields(containerId, json) {
    var container = document.getElementById(containerId);
    if (!container) return;
    var data = parseModelConfig(json);
    container.innerHTML = '';
    Object.keys(data).forEach(function(key) {
        addConfigKvRow(container, key, data[key]);
    });
    if (!Object.keys(data).length) {
        addConfigKvRow(container, 'threshold', '');
    }
}

function addConfigKvRow(container, key, value) {
    var row = document.createElement('div');
    row.className = 'config-kv-row';
    row.innerHTML = '<input type="text" class="form-select-sm cfg-key" placeholder="参数名" value="' + (key || '') + '">'
        + '<input type="text" class="form-select-sm cfg-val" placeholder="参数值" value="' + (value != null ? value : '') + '">'
        + '<button type="button" class="btn-outline-sm btn-sm" onclick="this.parentElement.remove()">删除</button>';
    container.appendChild(row);
}

function buildModelConfig(containerId) {
    var container = document.getElementById(containerId);
    var result = {};
    container.querySelectorAll('.config-kv-row').forEach(function(row) {
        var key = row.querySelector('.cfg-key').value.trim();
        var val = row.querySelector('.cfg-val').value.trim();
        if (!key) return;
        var num = parseFloat(val);
        result[key] = !isNaN(num) && String(num) === val ? num : val;
    });
    return JSON.stringify(result);
}

function parseIndicatorThreshold(json) {
    var data = {};
    try { data = json ? JSON.parse(json) : {}; } catch (e) { data = {}; }
    return {
        warning: data.warning != null ? data.warning : '',
        alert: data.alert != null ? data.alert : '',
        baseline: data.baseline != null ? data.baseline : ''
    };
}

function buildIndicatorThreshold(form) {
    var result = {};
    var w = form.warningThreshold.value;
    var a = form.alertThreshold.value;
    var b = form.baselineThreshold.value;
    if (w !== '') result.warning = parseFloat(w);
    if (a !== '') result.alert = parseFloat(a);
    if (b !== '') result.baseline = parseFloat(b);
    return JSON.stringify(result);
}

function fillIndicatorThreshold(form, json) {
    var t = parseIndicatorThreshold(json);
    form.warningThreshold.value = t.warning;
    form.alertThreshold.value = t.alert;
    form.baselineThreshold.value = t.baseline;
}
