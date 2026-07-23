#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""生成症候群监测系统四川本地化演示数据"""
import random
from datetime import datetime, timedelta
from pathlib import Path

random.seed(20260723)

# 基础数据已有 43 条病例，ID 从 44 开始
START_CASE_ID = 44

DISTRICTS_CD = [
    ("锦江区", "春熙路", "四川省人民医院"),
    ("青羊区", "宽窄巷子", "华西医院"),
    ("武侯区", "玉林街道", "成都市第三人民医院"),
    ("成华区", "建设路", "成都市第六人民医院"),
    ("金牛区", "一品天下", "成都市金牛区人民医院"),
    ("龙泉驿区", "十陵街道", "龙泉驿区第一人民医院"),
]
DISTRICTS_SC = [
    ("阿坝州", "马尔康市", "阿坝州人民医院"),
    ("甘孜州", "康定市", "甘孜州人民医院"),
    ("凉山州", "西昌市", "凉山州第一人民医院"),
    ("绵阳市", "涪城区", "绵阳市中心医院"),
    ("德阳市", "旌阳区", "德阳市人民医院"),
    ("宜宾市", "翠屏区", "宜宾市第一人民医院"),
]

SURNAMES = list("王李张刘陈杨黄赵周吴徐孙马朱胡郭何林罗高梁宋郑谢唐韩曹许邓萧冯曾程蔡彭潘袁于董余苏叶吕魏蒋田杜丁沈姜范江傅钟卢汪戴崔任陆廖姚方金邱夏谭韦贾邹石熊孟秦阎薛侯雷白龙段郝孔邵史毛万顾赖武康贺严尹钱施牛洪龚")
GIVEN_M = list("伟强磊军洋勇杰涛明超刚平辉鹏华斌波峰磊鑫宇浩凯博俊志文建国建华志强俊杰")
GIVEN_F = list("芳秀英敏静丽娟艳玲娜秀兰燕红霞梅莉萍颖慧雪琳婷倩雯欣怡蓉洁慧敏")
OCCUPATIONS = ["教师", "医生", "护士", "工人", "农民", "学生", "公务员", "司机", "个体户", "餐饮员", "程序员", "销售", "退休", "牧民", "兽医", "外卖员", "工程师", "会计", "幼儿"]
HOSPITALS = [
    "四川省人民医院", "华西医院", "成都市第三人民医院", "成都市公共卫生临床医疗中心",
    "成都市妇女儿童中心医院", "锦江区社区卫生服务中心", "武侯区玉林社区卫生服务中心",
    "成华区人民医院", "金牛区人民医院", "龙泉驿区第一人民医院",
    "阿坝州人民医院", "甘孜州人民医院", "凉山州第一人民医院", "绵阳市中心医院",
]
DIAGNOSES = {
    "发热呼吸道症候群": ["流感", "上呼吸道感染", "肺炎", "支气管炎", "新型冠状病毒感染"],
    "发热伴出血症候群": ["出血热疑似", "肾综合征出血热", "出血热"],
    "发热伴腹泻症候群": ["细菌性痢疾", "诺如病毒感染", "沙门氏菌感染", "急性肠胃炎", "轮状病毒感染", "食物中毒"],
    "发热出疹症候群": ["麻疹", "风疹", "猩红热", "幼儿急疹"],
    "脑炎脑膜炎症候群": ["病毒性脑炎", "化脓性脑膜炎", "结核性脑膜炎"],
    "不明原因发热症候群": ["不明原因发热", "发热待查"],
}
RISK_LEVELS = ["低风险", "中风险", "高风险", "待评估"]
DISCOVER = ["主动监测", "医院报告", "社区筛查", "学校报告"]
CASE_TYPES = ["疑似病例", "确诊病例", "临床诊断病例"]
WARNING_STATUSES = ["待研判", "已确认", "处置中", "已完成", "已关闭"]
WARNING_LEVELS = ["橙色", "红色", "黄色"]
ANOMALY_TYPES = ["异常增长", "持续上升", "场所聚集", "区域异常", "趋势异常", "重症异常", "死亡异常"]
EVENT_TYPES = ["场所聚集", "区域异常", "食源性风险", "学校聚集", "重症异常", "死亡异常"]
EVENT_STATUSES = ["待核查", "调查中", "处置中", "已完成", "已关闭"]

lines = []
lines.append("-- ========== 批量演示数据（四川） ==========")
lines.append("")

# 扩展症候群主题
lines.append("-- 扩展症候群主题")
lines.append("""INSERT INTO syndrome_config (syndrome_name, syndrome_code, definition, symptom_rules_json, risk_rules_json, monitor_model_json, status, description) VALUES
('发热出疹症候群', 'FBR', '以发热伴皮疹为主要表现的症候群，用于麻疹、风疹等出疹性传染病监测。', '{"required":["发热","皮疹"],"anyOf":["斑丘疹","疱疹","猩红热样皮疹"]}', '{"highRisk":["免疫缺陷","高热惊厥"],"mediumRisk":["婴幼儿","未接种疫苗"]}', '{"models":["固定阈值模型","EWMA指数加权移动平均模型"],"indicators":["皮疹构成","疫苗接种率"]}', '启用', '覆盖麻疹、风疹等出疹性疾病'),
('脑炎脑膜炎症候群', 'ENC', '以发热伴头痛、呕吐、意识障碍等中枢神经系统症状为主的症候群。', '{"required":["发热"],"anyOf":["头痛","呕吐","颈项强直","意识障碍","抽搐"]}', '{"highRisk":["昏迷","呼吸衰竭"],"mediumRisk":["持续头痛","脑膜刺激征阳性"]}', '{"models":["CUSUM累计和控制图模型","固定阈值模型"],"indicators":["脑脊液异常率","重症率"]}', '启用', '病毒性脑炎、化脓性脑膜炎等监测'),
('不明原因发热症候群', 'FUO', '持续发热超过规范时限且经初步检查未能明确病因的病例。', '{"required":["发热"],"anyOf":["持续发热≥7天","反复发热"]}', '{"highRisk":["免疫抑制","旅居史"],"mediumRisk":["常规检查阴性"]}', '{"models":["移动百分位模型","ARIMA模型"],"indicators":["待查病例数","确诊率"]}', '启用', '发热原因不明病例追踪');""")
lines.append("")

case_rows = []
symptom_rows = []
case_id = START_CASE_ID

def rand_name(gender):
    s = random.choice(SURNAMES)
    g = random.choice(GIVEN_M if gender == "男" else GIVEN_F)
    return s + g

def rand_date(start, end):
    delta = (end - start).days
    return start + timedelta(days=random.randint(0, max(delta, 1)))

def make_case(syndrome, district_info=None, bias=None):
    global case_id
    gender = random.choice(["男", "女"])
    age = random.randint(3, 78)
    if bias == "child":
        age = random.randint(3, 14)
    elif bias == "elderly":
        age = random.randint(65, 85)
    if district_info is None:
        if syndrome == "发热伴出血症候群":
            district_info = random.choice(DISTRICTS_SC[:3])
        elif syndrome in ("脑炎脑膜炎症候群",):
            district_info = random.choice(DISTRICTS_CD + DISTRICTS_SC[3:5])
        else:
            district_info = random.choice(DISTRICTS_CD + DISTRICTS_SC[3:])
    district, area, hospital = district_info
    name = rand_name(gender)
    idx = f"IDX{2026}{random.randint(4,7)}{case_id:04d}"
    addr = f"{district}{area}{random.randint(1,200)}号"
    report = rand_date(datetime(2025, 11, 1), datetime(2026, 7, 22))
    if random.random() < 0.08:
        report = datetime(2026, 7, 22) - timedelta(days=random.randint(0, 3))
    is_severe = 1 if random.random() < (0.18 if syndrome == "发热伴出血症候群" else 0.12) else 0
    is_death = 1 if is_severe and random.random() < 0.15 else 0
    if is_death:
        is_severe = 1
    if syndrome == "发热伴腹泻症候群":
        risk = random.choices(RISK_LEVELS, weights=[30, 35, 20, 15])[0]
    else:
        risk = random.choices(RISK_LEVELS[:3], weights=[55, 30, 15])[0]
    if is_severe and risk == "低风险":
        risk = "中风险"
    if is_death:
        risk = "高风险"
    fever = round(random.uniform(37.5, 40.2), 1)
    diag = random.choice(DIAGNOSES[syndrome])
    outcome = "死亡" if is_death else random.choice(["治愈", "好转", "治疗中"])
    occ = random.choice(OCCUPATIONS)
    if age < 18:
        occ = "学生" if age > 6 else "幼儿"
    if age > 60:
        occ = "退休"
    reason = None
    if risk == "高风险":
        reason = random.choice(["高龄合并基础疾病", "持续高热伴呼吸困难", "聚集性疫情关联", "血小板显著降低", "食源性暴露史", "免疫缺陷"])
    clinical = '{"respiratory":["咳嗽","咽痛"]}' if "呼吸道" in syndrome else '{"diarrhea":["腹泻","腹痛"]}' if "腹泻" in syndrome else '{"bleeding":["皮肤出血点"]}' if "出血" in syndrome else '{"rash":["斑丘疹"]}' if "出疹" in syndrome else '{"neuro":["头痛","呕吐"]}' if "脑炎" in syndrome else '{"fever":"持续发热"}'
    lab = '{"wbc":%.1f,"crp":%d}' % (random.uniform(3, 15), random.randint(5, 120))
    treat = '{"medication":["对症支持"]}'
    row = f"('{idx}', '{name}', '{gender}', {age}, '{occ}', '{random.choice(CASE_TYPES)}', '{syndrome}', '{addr}', '{district}', '{random.choice(DISCOVER)}', '{diag}', '{outcome}', {is_severe}, {is_death}, '{risk}', {('NULL' if not reason else repr(reason))}, '{report.strftime('%Y-%m-%d')}', '{hospital}', {fever}, '{clinical}', '{lab}', '{treat}')"
    case_rows.append(row)
    syms = ["发热"]
    if "呼吸道" in syndrome:
        syms += random.sample(["咳嗽", "咽痛", "流涕", "气促"], k=random.randint(1, 3))
    elif "腹泻" in syndrome:
        syms += random.sample(["腹泻", "腹痛", "呕吐", "恶心"], k=random.randint(1, 2))
    elif "出血" in syndrome:
        syms += random.sample(["出血", "皮肤出血点", "结膜出血"], k=random.randint(1, 2))
    elif "出疹" in syndrome:
        syms += ["皮疹"]
    elif "脑炎" in syndrome:
        syms += random.sample(["头痛", "呕吐", "意识障碍"], k=random.randint(1, 2))
    for s in syms:
        symptom_rows.append(f"({case_id}, '{s}')")
    cid = case_id
    case_id += 1
    return cid, syndrome, district, hospital, report, risk, name

# 生成病例：约 320 条
generated_meta = []
for _ in range(140):
    generated_meta.append(make_case("发热呼吸道症候群"))
for _ in range(35):
    generated_meta.append(make_case("发热伴出血症候群"))
for _ in range(45):
    generated_meta.append(make_case("发热伴腹泻症候群"))
for _ in range(25):
    generated_meta.append(make_case("发热出疹症候群"))
for _ in range(20):
    generated_meta.append(make_case("脑炎脑膜炎症候群"))
for _ in range(15):
    generated_meta.append(make_case("不明原因发热症候群"))

lines.append("-- 批量病例")
lines.append("INSERT INTO case_info (main_index, patient_name, gender, age, occupation, case_type, syndrome_type, address, district, discover_type, diagnosis, outcome, is_severe, is_death, risk_level, risk_reason, report_date, hospital, fever_temp, clinical_json, lab_json, treatment_json) VALUES")
for i, row in enumerate(case_rows):
    lines.append("  " + row + ("," if i < len(case_rows) - 1 else ";"))
lines.append("")

lines.append("-- 批量症状")
lines.append("INSERT INTO case_symptom (case_id, symptom_name) VALUES")
for i, row in enumerate(symptom_rows):
    lines.append("  " + row + ("," if i < len(symptom_rows) - 1 else ";"))
lines.append("")

# 预警记录 20 条
lines.append("-- 批量预警记录")
warn_rows = []
warn_meta = []
status_pool = ["待研判"]*6 + ["已确认"]*4 + ["处置中"]*3 + ["已完成"]*5 + ["已关闭"]*2
for i in range(20):
    model_id = random.randint(1, 11)
    syndromes = ["发热呼吸道症候群"]*8 + ["发热伴出血症候群"]*4 + ["发热伴腹泻症候群"]*4 + ["发热出疹症候群"]*2 + ["脑炎脑膜炎症候群"]*2
    syn = random.choice(syndromes)
    dist = random.choice([d[0] for d in DISTRICTS_CD + DISTRICTS_SC])
    hosp = random.choice(HOSPITALS)
    obs = round(random.uniform(3, 18), 1)
    base = round(obs * random.uniform(0.4, 0.8), 1)
    thr = round(base * random.uniform(1.1, 1.5), 1)
    st = status_pool[i % len(status_pool)]
    lvl = random.choice(WARNING_LEVELS)
    at = random.choice(ANOMALY_TYPES)
    wt = datetime(2026, 1, 15) + timedelta(days=random.randint(0, 180), hours=random.randint(8, 18))
    content = f"【模型预警】{syn}{dist}监测指标异常，观测值{obs:.0f}超过阈值{thr:.1f}"
    warn_rows.append(f"({model_id}, '{syn}', '{lvl}', '{content}', '{wt.strftime('%Y-%m-%d %H:%M:%S')}', '{st}', '{dist}', '{hosp}', {obs}, {base}, {thr}, '{random.choice(['中等','严重'])}', '{at}')")
    warn_meta.append((len(warn_rows), st, syn, dist))
lines.append("INSERT INTO warning_record (model_id, syndrome_type, warning_level, warning_content, warning_time, status, district, hospital, observed_value, baseline_value, threshold_value, anomaly_degree, anomaly_type) VALUES")
for i, row in enumerate(warn_rows):
    lines.append("  " + row + ("," if i < len(warn_rows) - 1 else ";"))
lines.append("")

# 预警通知（基础5条 + 新增，warning_id 从6起对应新插入；实际库中基础5条后新预警从id=6）
lines.append("-- 批量预警通知")
notify_targets = ["疾控业务人员", "监测分析人员", "处置人员", "分管领导", "值班人员"]
notify_methods = ["站内消息", "短信", "邮件", "电话"]
notif_rows = []
for wid in range(6, 26):  # 基础数据已有预警 id 1-5，批量新增 6-25
    for _ in range(random.randint(1, 3)):
        t = datetime(2026, 2, 1) + timedelta(days=random.randint(0, 150))
        notif_rows.append(f"({wid}, '{random.choice(notify_targets)}', '{random.choice(notify_methods)}', '{t.strftime('%Y-%m-%d %H:%M:%S')}', '已发送')")
lines.append("INSERT INTO warning_notification (warning_id, notify_target, notify_method, notify_time, notify_status) VALUES")
for i, row in enumerate(notif_rows):
    lines.append("  " + row + ("," if i < len(notif_rows) - 1 else ";"))
lines.append("")

# 预警处置
lines.append("-- 批量预警处置")
disp_rows = []
actions = ["确认异常", "启动处置", "完成处置", "关闭预警", "补充调查"]
for wid in range(1, 26):
    if wid in (2, 5):
        continue
    n = random.randint(1, 3)
    for j in range(n):
        t = datetime(2026, 2, 1) + timedelta(days=random.randint(0, 150))
        op = random.choice(["张业务", "系统管理员", "李浏览"])
        act = actions[min(j, len(actions)-1)]
        comments = ["经核实异常属实，已通知相关机构", "已完成现场流行病学调查", "病例搜索完毕，未发现新增", "监测指标已回落至基线水平", "已督促医疗机构完成信息报告"]
        disp_rows.append(f"({wid}, '{op}', '{t.strftime('%Y-%m-%d %H:%M:%S')}', '{act}', '{random.choice(comments)}')")
lines.append("INSERT INTO warning_disposal (warning_id, operator, action_time, action_type, action_comment) VALUES")
for i, row in enumerate(disp_rows):
    lines.append("  " + row + ("," if i < len(disp_rows) - 1 else ";"))
lines.append("")

# 监测事件
lines.append("-- 批量监测事件")
event_names = [
    ("锦江区春熙路商圈流感聚集", "场所聚集", "发热呼吸道症候群", "锦江区", "春熙路商圈"),
    ("成华区高校诺如病毒感染", "学校聚集", "发热伴腹泻症候群", "成华区", "某高校食堂"),
    ("武侯区重症肺炎病例增多", "重症异常", "发热呼吸道症候群", "武侯区", "多家医疗机构"),
    ("阿坝州出血热监测异常", "区域异常", "发热伴出血症候群", "阿坝州", "农牧区"),
    ("青羊区食源性腹泻事件", "食源性风险", "发热伴腹泻症候群", "青羊区", "某火锅店"),
    ("龙泉驿区幼儿园手足口病相关", "学校聚集", "发热出疹症候群", "龙泉驿区", "某幼儿园"),
    ("绵阳市脑炎病例报告", "区域异常", "脑炎脑膜炎症候群", "绵阳市", "市中心医院"),
    ("金牛区不明原因发热聚集", "区域异常", "不明原因发热症候群", "金牛区", "某社区"),
]
event_rows = []
for i, (ename, etype, esyn, edist, evenue) in enumerate(event_names):
    st = EVENT_STATUSES[i % len(EVENT_STATUSES)]
    dt = datetime(2026, 1, 20) + timedelta(days=i * 12)
    person = random.choice(["张业务", "系统管理员", "李浏览"])
    desc = f"{edist}{evenue}监测发现{esyn}相关异常，已开展流行病学调查。"
    event_rows.append(f"('{ename}', '{etype}', '{esyn}', '{edist}', '{evenue}', '{random.randint(44,300)},{random.randint(44,300)}', '{random.randint(6,25)}', '{st}', '{person}', '{dt.strftime('%Y-%m-%d %H:%M:%S')}', '{desc}')")
lines.append("INSERT INTO surveillance_event (event_name, event_type, syndrome_type, district, venue, related_cases, related_warnings, status, responsible_person, discovery_time, description) VALUES")
for i, row in enumerate(event_rows):
    lines.append("  " + row + ("," if i < len(event_rows) - 1 else ";"))
lines.append("")

# 报卡
lines.append("-- 批量报卡")
rc_rows = []
for cid in random.sample(range(START_CASE_ID, case_id), min(80, case_id - START_CASE_ID)):
    rd = datetime(2026, 1, 1) + timedelta(days=random.randint(0, 200))
    rc_rows.append(f"({cid}, 'BK{2026}{cid:06d}', '传染病报告卡', '{rd.strftime('%Y-%m-%d')}', '{random.choice(['张医生','李医生','王医生','赵医生','刘医生'])}', '{random.choice(['已审核','待审核','已上报'])}')")
lines.append("INSERT INTO report_card (case_id, card_no, report_type, report_date, reporter, status) VALUES")
for i, row in enumerate(rc_rows):
    lines.append("  " + row + ("," if i < len(rc_rows) - 1 else ";"))
lines.append("")

# 修改日志
lines.append("-- 批量修改日志")
log_rows = []
for cid in random.sample(range(1, case_id), 25):
    t = datetime(2026, 1, 1) + timedelta(days=random.randint(0, 200))
    descs = ["补充诊断信息", "更新风险等级", "修正患者联系方式", "完善临床表现描述", "更正报告日期", "补充检验结果"]
    log_rows.append(f"({cid}, '{random.choice(['张业务','系统管理员'])}', '{t.strftime('%Y-%m-%d %H:%M:%S')}', '{random.choice(descs)}')")
lines.append("INSERT INTO case_modify_log (case_id, operator, modify_time, change_desc) VALUES")
for i, row in enumerate(log_rows):
    lines.append("  " + row + ("," if i < len(log_rows) - 1 else ";"))
lines.append("")

# 操作日志
lines.append("-- 批量操作日志")
ops = ["用户登录", "查询病例列表", "导出病例数据", "运行预警分析", "预警处置", "修改病例信息", "查看病例详情", "条件树检索", "查看预警中心", "更新事件状态"]
op_rows = []
for i in range(40):
    t = datetime(2026, 6, 1) + timedelta(days=random.randint(0, 50), hours=random.randint(8, 20))
    op_rows.append(f"('{random.choice(['admin','business','viewer'])}', '{random.choice(ops)}', '192.168.{random.randint(1,5)}.{random.randint(10,250)}', '{t.strftime('%Y-%m-%d %H:%M:%S')}')")
lines.append("INSERT INTO operation_log (username, operation, ip, created_at) VALUES")
for i, row in enumerate(op_rows):
    lines.append("  " + row + ("," if i < len(op_rows) - 1 else ";"))
lines.append("")

# 保存查询
lines.append("-- 批量保存查询")
sq_rows = [
    "('武侯区中高风险呼吸道病例', '发热呼吸道症候群', '{\"logic\":\"AND\",\"conditions\":[{\"type\":\"district\",\"value\":\"武侯区\"},{\"type\":\"risk\",\"value\":\"中风险\"}]}', '张业务')",
    "('发热伴腹泻高风险', '发热伴腹泻症候群', '{\"logic\":\"AND\",\"conditions\":[{\"type\":\"syndrome\",\"value\":\"发热伴腹泻症候群\"},{\"type\":\"risk\",\"value\":\"高风险\"}]}', '张业务')",
    "('阿坝州出血病例', '发热伴出血症候群', '{\"logic\":\"AND\",\"conditions\":[{\"type\":\"district\",\"value\":\"阿坝州\"}]}', '系统管理员')",
    "('近期发热病例', NULL, '{\"logic\":\"AND\",\"conditions\":[{\"type\":\"fever\",\"operator\":\">\",\"value\":\"38.5\"}]}', '李浏览')",
    "('学龄儿童流感筛查', '发热呼吸道症候群', '{\"logic\":\"AND\",\"conditions\":[{\"type\":\"age\",\"operator\":\"<=\",\"value\":\"18\"},{\"type\":\"symptom\",\"value\":\"咳嗽\"}]}', '张业务')",
]
lines.append("INSERT INTO saved_query (query_name, syndrome_type, condition_json, created_by) VALUES")
for i, row in enumerate(sq_rows):
    lines.append("  " + row + ("," if i < len(sq_rows) - 1 else ";"))
lines.append("")

# 补充用户
lines.append("-- 补充用户")
lines.append("""INSERT INTO sys_user (username, password, role, real_name) VALUES
('analyst', 'analyst123', '业务人员', '王分析'),
('disposer', 'disposer123', '业务人员', '陈处置'),
('auditor', 'auditor123', '浏览人员', '赵审核');""")
lines.append("")

out = Path("/workspace/src/main/resources/data_bulk.sql")
out.write_text("\n".join(lines), encoding="utf-8")
print(f"Generated {case_id - START_CASE_ID} cases, {len(warn_rows)} warnings -> {out}")
