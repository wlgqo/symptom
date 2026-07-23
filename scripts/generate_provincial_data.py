#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""生成四川省各市州演示病例数据"""
import random
from datetime import datetime, timedelta
from pathlib import Path

random.seed(20260723)

CITIES = [
    ("绵阳市", "510700", "绵阳市中心医院"),
    ("德阳市", "510600", "德阳市人民医院"),
    ("宜宾市", "511500", "宜宾市第一人民医院"),
    ("乐山市", "511100", "乐山市人民医院"),
    ("南充市", "511300", "南充市中心医院"),
    ("泸州市", "510500", "泸州市人民医院"),
    ("自贡市", "510300", "自贡市第一人民医院"),
    ("攀枝花市", "510400", "攀枝花市中心医院"),
    ("广元市", "510800", "广元市中心医院"),
    ("遂宁市", "510900", "遂宁市中心医院"),
    ("内江市", "511000", "内江市第一人民医院"),
    ("眉山市", "511400", "眉山市人民医院"),
    ("广安市", "511600", "广安市人民医院"),
    ("达州市", "511700", "达州市中心医院"),
    ("雅安市", "511800", "雅安市人民医院"),
    ("巴中市", "511900", "巴中市中心医院"),
    ("资阳市", "512000", "资阳市人民医院"),
    ("阿坝州", "513200", "阿坝州人民医院"),
    ("甘孜州", "513300", "甘孜州人民医院"),
    ("凉山州", "513400", "凉山州第一人民医院"),
]

GEO_ALIAS = {
    "阿坝州": "阿坝藏族羌族自治州",
    "甘孜州": "甘孜藏族自治州",
    "凉山州": "凉山彝族自治州",
}

CITY_GEO_DISTRICT = {
    "绵阳市": "涪城区", "德阳市": "旌阳区", "宜宾市": "翠屏区", "乐山市": "市中区",
    "南充市": "顺庆区", "泸州市": "江阳区", "自贡市": "自流井区", "攀枝花市": "东区",
    "广元市": "利州区", "遂宁市": "船山区", "内江市": "市中区", "眉山市": "东坡区",
    "广安市": "广安区", "达州市": "通川区", "雅安市": "雨城区", "巴中市": "巴州区",
    "资阳市": "雁江区", "阿坝州": "马尔康市", "甘孜州": "康定市", "凉山州": "西昌市",
}

SURNAMES = list("王李张刘陈杨黄赵周吴徐孙马朱胡郭何林罗高梁宋郑谢唐韩曹许邓萧冯曾程蔡彭潘袁于董余苏叶吕魏蒋田杜丁沈姜范江傅钟卢汪戴崔任陆廖姚方金邱夏谭韦贾邹石熊孟秦阎薛侯雷白龙段郝孔邵史毛万顾赖武康贺严尹钱施牛洪龚")
GIVEN_M = list("伟强磊军洋勇杰涛明超刚平辉鹏华斌波峰鑫宇浩凯博俊志文建国建华志强俊杰")
GIVEN_F = list("芳秀英敏静丽娟艳玲娜秀兰燕红霞梅莉萍颖慧雪琳婷倩雯欣怡蓉洁慧敏")
OCCUPATIONS = ["教师", "医生", "护士", "工人", "农民", "学生", "公务员", "司机", "个体户", "餐饮员", "程序员", "销售", "退休", "工程师", "会计"]
SYNDROMES = [
    ("发热呼吸道症候群", 0.62),
    ("发热伴腹泻症候群", 0.12),
    ("发热伴出血症候群", 0.06),
    ("发热出疹症候群", 0.08),
    ("脑炎脑膜炎症候群", 0.07),
    ("不明原因发热症候群", 0.05),
]
DIAGNOSES = {
    "发热呼吸道症候群": ["流感", "上呼吸道感染", "肺炎", "支气管炎"],
    "发热伴腹泻症候群": ["诺如病毒感染", "细菌性痢疾", "急性肠胃炎", "沙门氏菌感染"],
    "发热伴出血症候群": ["出血热疑似", "肾综合征出血热"],
    "发热出疹症候群": ["麻疹", "风疹", "猩红热"],
    "脑炎脑膜炎症候群": ["病毒性脑炎", "化脓性脑膜炎"],
    "不明原因发热症候群": ["不明原因发热", "发热待查"],
}
TCM = {
    "发热呼吸道症候群": ["风热袭肺", "痰热壅肺", "肺卫气虚"],
    "发热伴腹泻症候群": ["湿热下注", "脾胃湿热", "寒湿困脾"],
    "发热伴出血症候群": ["血热妄行", "阴虚火旺", "气不摄血"],
    "发热出疹症候群": ["风热犯表", "邪郁肌表"],
    "脑炎脑膜炎症候群": ["热入营血", "肝风内动"],
    "不明原因发热症候群": ["邪伏膜原", "气虚发热"],
}
INFECTIOUS = {
    "流感": "流行性感冒", "肺炎": "社区获得性肺炎", "上呼吸道感染": "非传染病",
    "支气管炎": "非传染病", "诺如病毒感染": "诺如病毒感染", "细菌性痢疾": "细菌性痢疾",
    "急性肠胃炎": "非传染病", "沙门氏菌感染": "沙门氏菌感染",
    "出血热疑似": "肾综合征出血热（疑似）", "肾综合征出血热": "肾综合征出血热",
    "麻疹": "麻疹", "风疹": "风疹", "猩红热": "猩红热",
    "病毒性脑炎": "病毒性脑炎", "化脓性脑膜炎": "化脓性脑膜炎",
    "不明原因发热": "待排除传染病", "发热待查": "待排除传染病",
}
DISCOVER = ["主动监测", "医院报告", "社区筛查", "学校报告"]
CASE_TYPES = ["疑似病例", "确诊病例"]
RISK = ["低风险", "中风险", "高风险"]

DATE_START = datetime(2024, 8, 1)
DATE_END = datetime(2026, 7, 20)

case_id = 1
rows = []
updates = []


def rand_name(gender):
    s = random.choice(SURNAMES)
    g = random.choice(GIVEN_M if gender == "男" else GIVEN_F)
    return s + g


def pick_syndrome():
    r = random.random()
    acc = 0
    for syn, w in SYNDROMES:
        acc += w
        if r <= acc:
            return syn
    return SYNDROMES[0][0]


def clinical_json(syndrome):
    if "呼吸道" in syndrome:
        return '{"respiratory":["咳嗽","咽痛"],"accompany":["乏力"]}'
    if "腹泻" in syndrome:
        return '{"diarrhea":["水样便","腹痛"],"fever":"低热"}'
    if "出血" in syndrome:
        return '{"bleeding":["皮肤出血点"],"fever":"高热"}'
    if "出疹" in syndrome:
        return '{"rash":["斑丘疹"],"fever":"中热"}'
    if "脑炎" in syndrome:
        return '{"neuro":["头痛","呕吐"],"fever":"高热"}'
    return '{"fever":"持续发热"}'


def id_card(city_code, age, seq):
    year = 2026 - age
    month = random.randint(1, 12)
    day = random.randint(1, 28)
    return f"{city_code}{year:04d}{month:02d}{day:02d}{seq:04d}"


for city, adcode_prefix, hospital in CITIES:
    n = random.randint(7, 10)
    for _ in range(n):
        gender = random.choice(["男", "女"])
        age = random.randint(5, 72)
        name = rand_name(gender)
        syndrome = pick_syndrome()
        diag = random.choice(DIAGNOSES[syndrome])
        report_date = DATE_START + timedelta(days=random.randint(0, (DATE_END - DATE_START).days))
        idx = f"IDX{report_date.strftime('%Y%m')}P{case_id:03d}"
        area = CITY_GEO_DISTRICT.get(city, city)
        addr = f"{city}{area}{random.randint(1, 200)}号"
        occ = random.choice(OCCUPATIONS)
        if age < 18:
            occ = "学生"
        if age > 60:
            occ = "退休"
        is_severe = 1 if random.random() < 0.12 else 0
        is_death = 1 if is_severe and random.random() < 0.08 else 0
        risk = random.choices(RISK, weights=[60, 28, 12])[0]
        if is_death:
            risk = "高风险"
        outcome = "死亡" if is_death else random.choice(["治愈", "好转", "治疗中"])
        fever = round(random.uniform(37.6, 40.0), 1)
        reason = None
        if risk != "低风险":
            reason = random.choice(["持续高热", "聚集性关联", "职业暴露", "高龄基础病", "影像学异常"])
        lab = '{"wbc":%.1f,"crp":%d}' % (random.uniform(3.5, 14), random.randint(6, 100))
        treat = '{"medication":["对症支持"]}'
        rows.append(
            f"('{idx}', '{name}', '{gender}', {age}, '{occ}', '{random.choice(CASE_TYPES)}', "
            f"'{syndrome}', '{addr}', '{city}', '{random.choice(DISCOVER)}', '{diag}', '{outcome}', "
            f"{is_severe}, {is_death}, '{risk}', {repr(reason) if reason else 'NULL'}, "
            f"'{report_date.strftime('%Y-%m-%d')}', '{hospital}', {fever}, "
            f"'{clinical_json(syndrome)}', '{lab}', '{treat}')"
        )
        card = id_card(adcode_prefix[:4] + "01", age, case_id)
        phone = f"1{random.choice(['38','39','58','59','70','71','72','73'])}{random.randint(10000000, 99999999)}"
        tcm = random.choice(TCM[syndrome])
        inf = INFECTIOUS.get(diag, "待排除传染病")
        western = diag
        med = (
            f'{{"department":"内科","chiefComplaint":"发热就诊","westernDiagnosis":"{western}",'
            f'"tcmDiagnosis":"{tcm}","infectiousDiagnosis":"{inf}",'
            f'"idCard":"{card}","phone":"{phone}"}}'
        )
        updates.append(
            f"UPDATE case_info SET id_card='{card}', phone='{phone}', ethnicity='汉族', "
            f"medical_record_json='{med}' WHERE main_index='{idx}';"
        )
        case_id += 1

out = Path(__file__).resolve().parents[1] / "src/main/resources/data_provincial.sql"
lines = [
    "-- 四川省各市州演示病例（供省级用户全省地图下钻与区域筛选）",
    "INSERT INTO case_info (main_index, patient_name, gender, age, occupation, case_type, syndrome_type, address, district, discover_type, diagnosis, outcome, is_severe, is_death, risk_level, risk_reason, report_date, hospital, fever_temp, clinical_json, lab_json, treatment_json) VALUES",
]
for i, row in enumerate(rows):
    lines.append("  " + row + ("," if i < len(rows) - 1 else ";"))
lines.append("")
lines.extend(updates)
out.write_text("\n".join(lines) + "\n", encoding="utf-8")
print(f"Wrote {len(rows)} cases to {out}")
