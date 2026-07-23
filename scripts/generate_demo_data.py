#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""生成成都市症候群监测系统演示数据（长周期、密集分布）"""
import random
from datetime import datetime, timedelta
from pathlib import Path

random.seed(20260723)

START_CASE_ID = 44

DISTRICTS_CD = [
    ("锦江区", "春熙路", "四川省人民医院"),
    ("青羊区", "宽窄巷子", "华西医院"),
    ("武侯区", "玉林街道", "成都市第三人民医院"),
    ("成华区", "建设路", "成都市第六人民医院"),
    ("金牛区", "一品天下", "成都市金牛区人民医院"),
    ("龙泉驿区", "十陵街道", "龙泉驿区第一人民医院"),
    ("青白江区", "大弯街道", "青白江区人民医院"),
    ("新都区", "新都街道", "新都区人民医院"),
    ("温江区", "柳城街道", "温江区人民医院"),
    ("双流区", "东升街道", "双流区第一人民医院"),
    ("郫都区", "郫筒街道", "郫都区人民医院"),
    ("新津区", "五津街道", "新津区人民医院"),
]

# 武侯区权重更高，便于 cdc02 用户有足够数据
DISTRICT_WEIGHTS = [1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1]

SURNAMES = list("王李张刘陈杨黄赵周吴徐孙马朱胡郭何林罗高梁宋郑谢唐韩曹许邓萧冯曾程蔡彭潘袁于董余苏叶吕魏蒋田杜丁沈姜范江傅钟卢汪戴崔任陆廖姚方金邱夏谭韦贾邹石熊孟秦阎薛侯雷白龙段郝孔邵史毛万顾赖武康贺严尹钱施牛洪龚")
GIVEN_M = list("伟强磊军洋勇杰涛明超刚平辉鹏华斌波峰磊鑫宇浩凯博俊志文建国建华志强俊杰")
GIVEN_F = list("芳秀英敏静丽娟艳玲娜秀兰燕红霞梅莉萍颖慧雪琳婷倩雯欣怡蓉洁慧敏")
OCCUPATIONS = ["教师", "医生", "护士", "工人", "农民", "学生", "公务员", "司机", "个体户", "餐饮员", "程序员", "销售", "退休", "外卖员", "工程师", "会计", "幼儿"]
HOSPITALS = [h for _, _, h in DISTRICTS_CD] + [
    "成都市公共卫生临床医疗中心", "成都市妇女儿童中心医院",
    "锦江区社区卫生服务中心", "武侯区玉林社区卫生服务中心",
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
WARNING_STATUSES = ["待研判"] * 6 + ["已确认"] * 4 + ["处置中"] * 3 + ["已完成"] * 5 + ["已关闭"] * 2
WARNING_LEVELS = ["橙色", "红色", "黄色"]
ANOMALY_TYPES = ["异常增长", "持续上升", "场所聚集", "区域异常", "趋势异常", "重症异常", "死亡异常"]
EVENT_TYPES = ["场所聚集", "区域异常", "食源性风险", "学校聚集", "重症异常", "死亡异常"]
EVENT_STATUSES = ["待核查", "调查中", "处置中", "已完成", "已关闭"]

DATE_START = datetime(2024, 7, 1)
DATE_END = datetime(2026, 7, 22)

lines = []
lines.append("-- ========== 批量演示数据（成都市） ==========")
lines.append("")

lines.append("-- 扩展症候群主题")
lines.append("""INSERT INTO syndrome_config (syndrome_name, syndrome_code, definition, symptom_rules_json, risk_rules_json, monitor_model_json, status, description) VALUES
('发热出疹症候群', 'FBR', '以发热伴皮疹为主要表现的症候群，用于麻疹、风疹等出疹性传染病监测。', '{"logic":"AND","symptoms":{"required":["发热","皮疹"],"anyOf":["斑丘疹","疱疹","猩红热样皮疹"]},"signs":{"anyOf":["淋巴结肿大"]},"exclude":[]}', '{"highRisk":["免疫缺陷","高热惊厥"],"mediumRisk":["婴幼儿","未接种疫苗"]}', '{"models":["固定阈值模型","EWMA指数加权移动平均模型"],"indicators":["皮疹构成","疫苗接种率"]}', '启用', '覆盖麻疹、风疹等出疹性疾病'),
('脑炎脑膜炎症候群', 'ENC', '以发热伴头痛、呕吐、意识障碍等中枢神经系统症状为主的症候群。', '{"logic":"AND","symptoms":{"required":["发热"],"anyOf":["头痛","呕吐","颈项强直","意识障碍","抽搐"]},"signs":{"anyOf":["脑膜刺激征阳性","颈项强直"]},"lab":{"anyOf":["脑脊液异常"]}}', '{"highRisk":["昏迷","呼吸衰竭"],"mediumRisk":["持续头痛","脑膜刺激征阳性"]}', '{"models":["CUSUM累计和控制图模型","固定阈值模型"],"indicators":["脑脊液异常率","重症率"]}', '启用', '病毒性脑炎、化脓性脑膜炎等监测'),
('不明原因发热症候群', 'FUO', '持续发热超过规范时限且经初步检查未能明确病因的病例。', '{"logic":"AND","symptoms":{"required":["发热"],"anyOf":["持续发热≥7天","反复发热"]},"signs":{"anyOf":["体温≥38℃持续"]}}', '{"highRisk":["免疫抑制","旅居史"],"mediumRisk":["常规检查阴性"]}', '{"models":["移动百分位模型","ARIMA模型"],"indicators":["待查病例数","确诊率"]}', '启用', '发热原因不明病例追踪');""")
lines.append("")

case_rows = []
symptom_rows = []
case_id = START_CASE_ID
generated_meta = []


def rand_name(gender):
    s = random.choice(SURNAMES)
    g = random.choice(GIVEN_M if gender == "男" else GIVEN_F)
    return s + g


def weighted_district():
    return random.choices(DISTRICTS_CD, weights=DISTRICT_WEIGHTS, k=1)[0]


def make_case(syndrome, report_date, district_info=None):
    global case_id
    gender = random.choice(["男", "女"])
    age = random.randint(3, 78)
    if district_info is None:
        district_info = weighted_district()
    district, area, hospital = district_info
    name = rand_name(gender)
    idx = f"IDX{report_date.strftime('%Y%m')}{case_id:05d}"
    addr = f"{district}{area}{random.randint(1, 200)}号"
    is_severe = 1 if random.random() < (0.14 if syndrome == "发热伴出血症候群" else 0.10) else 0
    is_death = 1 if is_severe and random.random() < 0.12 else 0
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
    row = f"('{idx}', '{name}', '{gender}', {age}, '{occ}', '{random.choice(CASE_TYPES)}', '{syndrome}', '{addr}', '{district}', '{random.choice(DISCOVER)}', '{diag}', '{outcome}', {is_severe}, {is_death}, '{risk}', {('NULL' if not reason else repr(reason))}, '{report_date.strftime('%Y-%m-%d')}', '{hospital}', {fever}, '{clinical}', '{lab}', '{treat}')"
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
    return cid, syndrome, district, hospital, report_date, risk, name


# 按日生成病例，保证时间序列密集
syndrome_pool = (
    ["发热呼吸道症候群"] * 55
    + ["发热伴腹泻症候群"] * 15
    + ["发热伴出血症候群"] * 8
    + ["发热出疹症候群"] * 10
    + ["脑炎脑膜炎症候群"] * 7
    + ["不明原因发热症候群"] * 5
)

current = DATE_START
while current <= DATE_END:
    # 工作日略多，周末略少，冬季呼吸道略多
    base = random.randint(3, 7)
    if current.weekday() >= 5:
        base = max(2, base - 1)
    if current.month in (11, 12, 1, 2):
        base += random.randint(0, 2)
    for _ in range(base):
        syn = random.choice(syndrome_pool)
        if current.month in (11, 12, 1, 2) and random.random() < 0.15:
            syn = "发热呼吸道症候群"
        generated_meta.append(make_case(syn, current))
    current += timedelta(days=1)

def write_batched_insert(table_name, columns, rows, batch_size=200):
    if not rows:
        return
    lines.append(f"-- {table_name}")
    for start in range(0, len(rows), batch_size):
        chunk = rows[start:start + batch_size]
        lines.append(f"INSERT INTO {table_name} ({columns}) VALUES")
        for i, row in enumerate(chunk):
            lines.append("  " + row + ("," if i < len(chunk) - 1 else ";"))
        lines.append("")

write_batched_insert(
    "case_info",
    "main_index, patient_name, gender, age, occupation, case_type, syndrome_type, address, district, discover_type, diagnosis, outcome, is_severe, is_death, risk_level, risk_reason, report_date, hospital, fever_temp, clinical_json, lab_json, treatment_json",
    case_rows,
)
write_batched_insert("case_symptom", "case_id, symptom_name", symptom_rows, batch_size=500)

# 预警记录
lines.append("-- 批量预警记录")
warn_rows = []
status_pool = WARNING_STATUSES
for i in range(35):
    model_id = random.randint(1, 11)
    syndromes = ["发热呼吸道症候群"] * 14 + ["发热伴出血症候群"] * 5 + ["发热伴腹泻症候群"] * 8 + ["发热出疹症候群"] * 4 + ["脑炎脑膜炎症候群"] * 4
    syn = random.choice(syndromes)
    dist = random.choice([d[0] for d in DISTRICTS_CD])
    hosp = random.choice(HOSPITALS)
    obs = round(random.uniform(3, 18), 1)
    base = round(obs * random.uniform(0.4, 0.8), 1)
    thr = round(base * random.uniform(1.1, 1.5), 1)
    st = status_pool[i % len(status_pool)]
    lvl = random.choice(WARNING_LEVELS)
    at = random.choice(ANOMALY_TYPES)
    wt = DATE_START + timedelta(days=random.randint(60, 720), hours=random.randint(8, 18))
    content = f"【模型预警】{syn}{dist}监测指标异常，观测值{obs:.0f}超过阈值{thr:.1f}"
    warn_rows.append(f"({model_id}, '{syn}', '{lvl}', '{content}', '{wt.strftime('%Y-%m-%d %H:%M:%S')}', '{st}', '{dist}', '{hosp}', {obs}, {base}, {thr}, '{random.choice(['中等','严重'])}', '{at}')")
lines.append("INSERT INTO warning_record (model_id, syndrome_type, warning_level, warning_content, warning_time, status, district, hospital, observed_value, baseline_value, threshold_value, anomaly_degree, anomaly_type) VALUES")
for i, row in enumerate(warn_rows):
    lines.append("  " + row + ("," if i < len(warn_rows) - 1 else ";"))
lines.append("")

lines.append("-- 批量预警通知")
notify_targets = ["疾控业务人员", "监测分析人员", "处置人员", "分管领导", "值班人员"]
notify_methods = ["站内消息", "短信", "邮件", "电话"]
notif_rows = []
for wid in range(6, 41):
    for _ in range(random.randint(1, 2)):
        t = DATE_START + timedelta(days=random.randint(90, 700))
        notif_rows.append(f"({wid}, '{random.choice(notify_targets)}', '{random.choice(notify_methods)}', '{t.strftime('%Y-%m-%d %H:%M:%S')}', '已发送')")
lines.append("INSERT INTO warning_notification (warning_id, notify_target, notify_method, notify_time, notify_status) VALUES")
for i, row in enumerate(notif_rows):
    lines.append("  " + row + ("," if i < len(notif_rows) - 1 else ";"))
lines.append("")

lines.append("-- 批量预警处置")
disp_rows = []
actions = ["确认异常", "启动处置", "完成处置", "关闭预警", "补充调查"]
for wid in range(1, 41):
    if wid in (2, 5):
        continue
    n = random.randint(1, 2)
    for j in range(n):
        t = DATE_START + timedelta(days=random.randint(90, 700))
        op = random.choice(["cdc01", "cdc02", "系统管理员"])
        act = actions[min(j, len(actions) - 1)]
        comments = ["经核实异常属实，已通知相关机构", "已完成现场流行病学调查", "病例搜索完毕，未发现新增", "监测指标已回落至基线水平", "已督促医疗机构完成信息报告"]
        disp_rows.append(f"({wid}, '{op}', '{t.strftime('%Y-%m-%d %H:%M:%S')}', '{act}', '{random.choice(comments)}')")
lines.append("INSERT INTO warning_disposal (warning_id, operator, action_time, action_type, action_comment) VALUES")
for i, row in enumerate(disp_rows):
    lines.append("  " + row + ("," if i < len(disp_rows) - 1 else ";"))
lines.append("")

lines.append("-- 批量监测事件")
event_names = [
    ("锦江区春熙路商圈流感聚集", "场所聚集", "发热呼吸道症候群", "锦江区", "春熙路商圈"),
    ("成华区高校诺如病毒感染", "学校聚集", "发热伴腹泻症候群", "成华区", "某高校食堂"),
    ("武侯区重症肺炎病例增多", "重症异常", "发热呼吸道症候群", "武侯区", "多家医疗机构"),
    ("双流区出血热监测异常", "区域异常", "发热伴出血症候群", "双流区", "城郊结合部"),
    ("青羊区食源性腹泻事件", "食源性风险", "发热伴腹泻症候群", "青羊区", "某火锅店"),
    ("龙泉驿区幼儿园出疹病例", "学校聚集", "发热出疹症候群", "龙泉驿区", "某幼儿园"),
    ("温江区脑炎病例报告", "区域异常", "脑炎脑膜炎症候群", "温江区", "区人民医院"),
    ("金牛区不明原因发热聚集", "区域异常", "不明原因发热症候群", "金牛区", "某社区"),
    ("武侯区玉林街道呼吸道聚集", "场所聚集", "发热呼吸道症候群", "武侯区", "玉林街道"),
    ("新都区腹泻聚集预警", "食源性风险", "发热伴腹泻症候群", "新都区", "某农贸市场"),
]
event_rows = []
for i, (ename, etype, esyn, edist, evenue) in enumerate(event_names):
    st = EVENT_STATUSES[i % len(EVENT_STATUSES)]
    dt = DATE_START + timedelta(days=60 + i * 45)
    person = random.choice(["cdc01", "cdc02", "系统管理员"])
    desc = f"{edist}{evenue}监测发现{esyn}相关异常，已开展流行病学调查。"
    event_rows.append(f"('{ename}', '{etype}', '{esyn}', '{edist}', '{evenue}', '{random.randint(44, 2000)},{random.randint(44, 2000)}', '{random.randint(6, 35)}', '{st}', '{person}', '{dt.strftime('%Y-%m-%d %H:%M:%S')}', '{desc}')")
lines.append("INSERT INTO surveillance_event (event_name, event_type, syndrome_type, district, venue, related_cases, related_warnings, status, responsible_person, discovery_time, description) VALUES")
for i, row in enumerate(event_rows):
    lines.append("  " + row + ("," if i < len(event_rows) - 1 else ";"))
lines.append("")

lines.append("-- 批量报卡")
rc_rows = []
for cid in random.sample(range(START_CASE_ID, case_id), min(150, case_id - START_CASE_ID)):
    rd = DATE_START + timedelta(days=random.randint(0, 720))
    rc_rows.append(f"({cid}, 'BK{2026}{cid:06d}', '传染病报告卡', '{rd.strftime('%Y-%m-%d')}', '{random.choice(['张医生','李医生','王医生','赵医生','刘医生'])}', '{random.choice(['已审核','待审核','已上报'])}')")
lines.append("INSERT INTO report_card (case_id, card_no, report_type, report_date, reporter, status) VALUES")
for i, row in enumerate(rc_rows):
    lines.append("  " + row + ("," if i < len(rc_rows) - 1 else ";"))
lines.append("")

lines.append("-- 批量修改日志")
log_rows = []
for cid in random.sample(range(1, case_id), 40):
    t = DATE_START + timedelta(days=random.randint(0, 700))
    descs = ["补充诊断信息", "更新风险等级", "修正患者联系方式", "完善临床表现描述", "更正报告日期", "补充检验结果"]
    log_rows.append(f"({cid}, '{random.choice(['cdc01','cdc02','系统管理员'])}', '{t.strftime('%Y-%m-%d %H:%M:%S')}', '{random.choice(descs)}')")
lines.append("INSERT INTO case_modify_log (case_id, operator, modify_time, change_desc) VALUES")
for i, row in enumerate(log_rows):
    lines.append("  " + row + ("," if i < len(log_rows) - 1 else ";"))
lines.append("")

lines.append("-- 批量操作日志")
ops = ["用户登录", "查询病例列表", "导出病例数据", "运行预警分析", "预警处置", "修改病例信息", "查看病例详情", "条件树检索", "查看预警中心", "更新事件状态"]
op_rows = []
for i in range(50):
    t = datetime(2026, 1, 1) + timedelta(days=random.randint(0, 200), hours=random.randint(8, 20))
    op_rows.append(f"('{random.choice(['admin','cdc01','cdc02','viewer'])}', '{random.choice(ops)}', '192.168.{random.randint(1,5)}.{random.randint(10,250)}', '{t.strftime('%Y-%m-%d %H:%M:%S')}')")
lines.append("INSERT INTO operation_log (username, operation, ip, created_at) VALUES")
for i, row in enumerate(op_rows):
    lines.append("  " + row + ("," if i < len(op_rows) - 1 else ";"))
lines.append("")

lines.append("-- 批量保存查询")
sq_rows = [
    "('武侯区中高风险呼吸道病例', '发热呼吸道症候群', '{\"logic\":\"AND\",\"conditions\":[{\"type\":\"district\",\"value\":\"武侯区\"},{\"type\":\"risk\",\"value\":\"中风险\"}]}', 'cdc02')",
    "('全市发热伴腹泻高风险', '发热伴腹泻症候群', '{\"logic\":\"AND\",\"conditions\":[{\"type\":\"syndrome\",\"value\":\"发热伴腹泻症候群\"},{\"type\":\"risk\",\"value\":\"高风险\"}]}', 'cdc01')",
    "('双流区出血病例', '发热伴出血症候群', '{\"logic\":\"AND\",\"conditions\":[{\"type\":\"district\",\"value\":\"双流区\"}]}', 'cdc01')",
    "('近期发热病例', NULL, '{\"logic\":\"AND\",\"conditions\":[{\"type\":\"fever\",\"operator\":\">\",\"value\":\"38.5\"}]}', 'viewer')",
    "('学龄儿童流感筛查', '发热呼吸道症候群', '{\"logic\":\"AND\",\"conditions\":[{\"type\":\"age\",\"operator\":\"<=\",\"value\":\"18\"},{\"type\":\"symptom\",\"value\":\"咳嗽\"}]}', 'cdc02')",
]
lines.append("INSERT INTO saved_query (query_name, syndrome_type, condition_json, created_by) VALUES")
for i, row in enumerate(sq_rows):
    lines.append("  " + row + ("," if i < len(sq_rows) - 1 else ";"))
lines.append("")

out = Path("/workspace/src/main/resources/data_bulk.sql")
out.write_text("\n".join(lines), encoding="utf-8")
print(f"Generated {case_id - START_CASE_ID} cases, {len(warn_rows)} warnings -> {out}")
