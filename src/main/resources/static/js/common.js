document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.tab-nav').forEach(function(nav) {
        nav.querySelectorAll('.tab-item').forEach(function(tab) {
            tab.addEventListener('click', function() {
                var tabId = tab.dataset.tab;
                var parent = nav.parentElement;
                nav.querySelectorAll('.tab-item').forEach(function(t) { t.classList.remove('active'); });
                parent.querySelectorAll('.tab-content').forEach(function(c) { c.classList.remove('active'); });
                tab.classList.add('active');
                var content = parent.querySelector('#tab-' + tabId);
                if (content) content.classList.add('active');
                window.dispatchEvent(new Event('resize'));
            });
        });
    });

    document.getElementById('sidebarToggle')?.addEventListener('click', function() {
        document.getElementById('sidebar').classList.toggle('collapsed');
    });

    document.querySelectorAll('.nav-group-title').forEach(function(btn) {
        btn.addEventListener('click', function() {
            var group = btn.closest('.nav-group');
            if (!group) return;
            group.classList.toggle('collapsed');
            group.classList.toggle('expanded');
            btn.setAttribute('aria-expanded', group.classList.contains('expanded'));
        });
    });

    initRegionHospitalCascade();
});

function initRegionHospitalCascade() {
    var hospitalMap = window.__hospitalMap || {};
    document.querySelectorAll('.region-select').forEach(function(regionSel) {
        if (regionSel.dataset.cascadeBound) return;
        regionSel.dataset.cascadeBound = '1';
        regionSel.addEventListener('change', function() {
            var form = regionSel.closest('form');
            var hospitalSel = form ? form.querySelector('.hospital-select') : null;
            if (!hospitalSel || hospitalSel.disabled) return;
            var district = regionSel.value;
            var hospitals = hospitalMap[district] || [];
            var current = hospitalSel.value;
            hospitalSel.innerHTML = '<option value="">全部机构</option>';
            hospitals.forEach(function(h) {
                var opt = document.createElement('option');
                opt.value = h;
                opt.textContent = h;
                if (h === current) opt.selected = true;
                hospitalSel.appendChild(opt);
            });
        });
    });
}

function renderMedicalRecordSummary(medicalJson, treatmentJson) {
    var el = document.getElementById('medicalRecordSummary');
    if (!el) return;
    try {
        var medical = typeof medicalJson === 'string' ? JSON.parse(medicalJson) : (medicalJson || {});
        var html = '<div class="detail-grid">';
        html += '<div class="detail-item full-width"><span class="label">主诉</span><span class="value">' + esc(medical.chiefComplaint || '-') + '</span></div>';
        html += '<div class="detail-item full-width"><span class="label">现病史</span><span class="value">' + esc(medical.presentIllness || '-') + '</span></div>';
        html += '<div class="detail-item full-width"><span class="label">体格检查</span><span class="value">' + esc(medical.physicalExam || '-') + '</span></div>';
        if (medical.treatmentPlan) {
            html += '<div class="detail-item full-width"><span class="label">诊疗方案</span><span class="value">' + esc(medical.treatmentPlan) + '</span></div>';
        } else if (treatmentJson) {
            try {
                var t = typeof treatmentJson === 'string' ? JSON.parse(treatmentJson) : treatmentJson;
                if (t.medication) {
                    html += '<div class="detail-item full-width"><span class="label">用药记录</span><span class="value">' + esc(t.medication.join('、')) + '</span></div>';
                }
            } catch (ignore) {}
        }
        if (medical.department) {
            html += '<div class="detail-item"><span class="label">科室</span><span class="value">' + esc(medical.department) + '</span></div>';
        }
        if (medical.bedNo) {
            html += '<div class="detail-item"><span class="label">床号/病区</span><span class="value">' + esc(medical.bedNo) + '</span></div>';
        }
        html += '</div>';

        var auxItems = [];
        (medical.examinations || []).forEach(function(e) {
            auxItems.push({date: e.date, type: '辅助检查', name: e.item, result: e.result});
        });
        (medical.labTests || []).forEach(function(l) {
            auxItems.push({date: l.date, type: '实验室检查', name: l.item, result: l.result});
        });
        if (auxItems.length > 0) {
            html += '<h6 style="margin:16px 0 8px;font-size:13px;color:#4a5568;">辅助检查</h6>';
            html += '<table class="data-table compact"><thead><tr><th>日期</th><th>类型</th><th>项目</th><th>结果</th></tr></thead><tbody>';
            auxItems.forEach(function(item) {
                html += '<tr><td>' + (item.date || '-') + '</td><td>' + item.type + '</td><td>' + item.name + '</td><td>' + (item.result || '-') + '</td></tr>';
            });
            html += '</tbody></table>';
        } else {
            html += '<p class="empty-hint" style="margin-top:12px;">暂无辅助检查记录</p>';
        }
        el.innerHTML = html;
    } catch (e) {
        el.innerHTML = '<p class="empty-state">病历信息加载失败</p>';
    }
}

function esc(text) {
    if (text == null) return '';
    return String(text).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}

function renderPatientProfile(profileJson, medicalJson) {
    var profileEl = document.getElementById('patientProfileContent');
    var timelineEl = document.getElementById('medicalTimeline');
    if (!profileEl) return;

    var profile = {};
    var medical = {};
    try {
        profile = profileJson ? (typeof profileJson === 'string' ? JSON.parse(profileJson) : profileJson) : {};
    } catch (e) {
        profileEl.innerHTML = '<p class="empty-state">患者画像数据加载失败</p>';
        if (timelineEl) timelineEl.innerHTML = '<p class="empty-state">暂无诊疗活动记录</p>';
        return;
    }
    try {
        medical = medicalJson ? (typeof medicalJson === 'string' ? JSON.parse(medicalJson) : medicalJson) : {};
    } catch (e) {
        medical = {};
    }

    try {

        var tagsHtml = (profile.tags || []).map(function(t) {
            return '<span class="tag-item">' + t + '</span>';
        }).join('');

        var chronicHtml = (profile.chronicDiseases || []).length > 0
            ? profile.chronicDiseases.join('、') : '无';
        var allergyHtml = (profile.allergies || []).length > 0
            ? profile.allergies.join('、') : '无';
        var vaccHtml = (profile.vaccination || []).length > 0
            ? profile.vaccination.join('、') : '无';

        profileEl.innerHTML =
            '<div class="detail-grid">' +
            '<div class="detail-item"><span class="label">血型</span><span class="value">' + (profile.bloodType || '-') + '</span></div>' +
            '<div class="detail-item"><span class="label">婚姻状况</span><span class="value">' + (profile.maritalStatus || '-') + '</span></div>' +
            '<div class="detail-item"><span class="label">慢性病史</span><span class="value">' + chronicHtml + '</span></div>' +
            '<div class="detail-item"><span class="label">过敏史</span><span class="value">' + allergyHtml + '</span></div>' +
            '<div class="detail-item"><span class="label">疫苗接种</span><span class="value">' + vaccHtml + '</span></div>' +
            '</div>' +
            (profile.portrait ? '<p style="margin-top:12px;font-size:13px;color:#718096;line-height:1.7;padding:12px;background:#f7fafc;border-radius:6px;">' + profile.portrait + '</p>' : '') +
            (tagsHtml ? '<div class="tag-list" style="margin-top:12px;">' + tagsHtml + '</div>' : '');

        if (timelineEl) {
            var items = [];
            (medical.visits || []).forEach(function(v) {
                items.push({date: v.date, title: v.type + ' - ' + (v.dept || ''), desc: v.doctor ? '接诊医生：' + v.doctor : ''});
            });
            (medical.examinations || []).forEach(function(e) {
                items.push({date: e.date, title: '检查：' + e.item, desc: e.result});
            });
            (medical.labTests || []).forEach(function(l) {
                items.push({date: l.date, title: '检验：' + l.item, desc: l.result});
            });
            if (medical.deathInfo) {
                items.push({date: medical.deathInfo.deathDate, title: '死亡记录', desc: medical.deathInfo.deathCause, death: true});
            }
            items.sort(function(a, b) { return (a.date || '').localeCompare(b.date || ''); });

            if (items.length === 0) {
                timelineEl.innerHTML = '<p class="empty-state">暂无诊疗活动记录</p>';
            } else {
                timelineEl.innerHTML = '<div class="timeline">' + items.map(function(item) {
                    return '<div class="timeline-item' + (item.death ? ' death' : '') + '">' +
                        '<div class="timeline-date">' + item.date + '</div>' +
                        '<div class="timeline-title">' + item.title + '</div>' +
                        (item.desc ? '<div class="timeline-desc">' + item.desc + '</div>' : '') +
                        '</div>';
                }).join('') + '</div>';
            }
        }
    } catch (e) {
        profileEl.innerHTML = '<p class="empty-state">患者画像数据加载失败</p>';
        if (timelineEl) timelineEl.innerHTML = '<p class="empty-state">暂无诊疗活动记录</p>';
    }
}

/** 饼图：引导线外显数值标签 */
function buildLabeledPieOption(data, options) {
    options = options || {};
    var seriesData = (data || []).filter(function(d) {
        return d && (d.value == null ? 0 : Number(d.value)) > 0;
    }).map(function(d) {
        return { name: d.name, value: Number(d.value) };
    });
    var isCompact = !!options.compact;
    return {
        tooltip: { trigger: 'item', formatter: '{b}<br/>{c}例 ({d}%)' },
        color: options.colors || ['#1677ff', '#52c41a', '#faad14', '#13c2c2', '#722ed1', '#ff4d4f', '#eb2f96', '#2f54eb'],
        legend: options.showLegend === false ? undefined : {
            type: 'scroll', bottom: 0, itemWidth: 10, itemHeight: 10, textStyle: { fontSize: 10 }
        },
        series: [{
            type: 'pie',
            radius: options.radius || (isCompact ? ['28%', '48%'] : ['32%', '55%']),
            center: options.center || ['50%', isCompact ? '46%' : '48%'],
            avoidLabelOverlap: true,
            itemStyle: { borderRadius: 3, borderColor: '#fff', borderWidth: 1 },
            label: {
                show: true,
                position: 'outside',
                formatter: '{b}\n{c}例 {d}%',
                fontSize: isCompact ? 10 : 11,
                lineHeight: 14,
                color: '#4a5568'
            },
            labelLine: {
                show: true,
                length: isCompact ? 8 : 12,
                length2: isCompact ? 6 : 10,
                smooth: true,
                lineStyle: { color: '#cbd5e0' }
            },
            emphasis: {
                label: { show: true, fontSize: isCompact ? 11 : 12, fontWeight: 'bold' }
            },
            data: seriesData
        }]
    };
}

function mapObjectToPieData(obj) {
    return Object.keys(obj || {}).map(function(k) { return { name: k, value: obj[k] }; });
}

function initLabeledPieChart(domId, data, options) {
    var el = document.getElementById(domId);
    if (!el || typeof echarts === 'undefined') return null;
    var chart = echarts.init(el);
    chart.setOption(buildLabeledPieOption(data, options));
    return chart;
}

function renderClinicalJson(clinicalJson, syndromeType) {
    var el = document.getElementById('clinicalDetail');
    if (!el || !clinicalJson) return;
    try {
        var data = JSON.parse(clinicalJson);
        var html = '<div class="detail-grid">';
        if (data.respiratory) {
            html += '<div class="detail-item"><span class="label">呼吸道症状</span><span class="value">' + data.respiratory.join('、') + '</span></div>';
        }
        if (data.bleeding) {
            html += '<div class="detail-item"><span class="label">出血症状</span><span class="value">' + data.bleeding.join('、') + '</span></div>';
        }
        if (data.diarrhea) {
            html += '<div class="detail-item"><span class="label">腹泻症状</span><span class="value">' + data.diarrhea.join('、') + '</span></div>';
        }
        if (data.accompany) {
            html += '<div class="detail-item"><span class="label">伴随症状</span><span class="value">' + data.accompany.join('、') + '</span></div>';
        }
        if (data.fever) {
            html += '<div class="detail-item"><span class="label">发热描述</span><span class="value">' + data.fever + '</span></div>';
        }
        html += '</div>';
        el.innerHTML = html;
    } catch (e) {
        el.innerHTML = '<pre class="json-display">' + clinicalJson + '</pre>';
    }
}

function renderLabJson(labJson) {
    var el = document.getElementById('labDetail');
    if (!el || !labJson) return;
    try {
        var data = JSON.parse(labJson);
        var html = '<div class="detail-grid">';
        for (var key in data) {
            if (data.hasOwnProperty(key)) {
                var label = key;
                var map = {wbc:'白细胞',crp:'C反应蛋白',platelet:'血小板',stool:'粪便检测'};
                if (map[key]) label = map[key];
                html += '<div class="detail-item"><span class="label">' + label + '</span><span class="value">' + data[key] + '</span></div>';
            }
        }
        html += '</div>';
        el.innerHTML = html;
    } catch (e) {
        el.innerHTML = '<pre class="json-display">' + labJson + '</pre>';
    }
}
