# 症候群监测管理系统 (SSEW)

症候群监测管理系统（Syndromic Surveillance & Early Warning Platform）面向疾病预防控制机构的 POC 演示系统，覆盖监测驾驶舱、症候群专题监测、预警中心、病例中心、智能分析、监测配置等完整业务链路。

## 技术栈

- 后端：Java 8+、Spring Boot 2.7、MyBatis、Thymeleaf
- 前端：HTML5、CSS3、JavaScript、ECharts
- 数据库：SQLite

## 功能模块

| 模块 | 路径 | 说明 |
|------|------|------|
| 监测驾驶舱 | `/` | KPI、趋势、区域分布、异常事件、重点病例 |
| 发热呼吸道症候群 | `/respiratory` | 三间分布、临床特征、预警模型 |
| 发热伴出血症候群 | `/hemorrhage` | 重症/死亡监测 |
| 发热伴腹泻症候群 | `/diarrhea` | 高风险疑似病例筛查 |
| 症候群主题库 | `/theme` | 主题定义与条件树规则 |
| 预警中心 | `/warning/center` | 三栏预警工作台（列表/详情/处置） |
| 预警模型中心 | `/warning/model` | 7类预警模型管理与运行 |
| 事件中心 | `/event` | 异常事件跟踪 |
| 病例中心 | `/case/list` | 查询、编辑、导出、360°画像 |
| 条件树分析 | `/search` | 可视化条件树组合查询 |
| 监测配置 | `/config` | 症候群定义、预警模型配置 |
| 系统管理 | `/admin` | 用户、日志管理 |

## 演示账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 管理员 |
| business | business123 | 业务人员 |
| viewer | viewer123 | 浏览人员 |

## 启动方式

```bash
mkdir -p data
mvn spring-boot:run
```

访问 http://localhost:8080

## 核心演示链路

登录 → 监测驾驶舱 → 发现异常 → 预警中心处置 → 查看关联病例 → 病例360°画像 → 症候群专题分析 → 主题库条件树 → 病例查询导出
