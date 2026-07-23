-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    real_name VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 症候群配置表
CREATE TABLE IF NOT EXISTS syndrome_config (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    syndrome_name VARCHAR(50) NOT NULL,
    syndrome_code VARCHAR(20) NOT NULL,
    definition TEXT,
    symptom_rules_json TEXT,
    risk_rules_json TEXT,
    monitor_model_json TEXT,
    status VARCHAR(20) DEFAULT '启用',
    description TEXT
);

-- 病例信息表
CREATE TABLE IF NOT EXISTS case_info (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    main_index VARCHAR(50) NOT NULL,
    patient_name VARCHAR(50) NOT NULL,
    gender VARCHAR(10),
    age INTEGER,
    occupation VARCHAR(50),
    id_card VARCHAR(20),
    phone VARCHAR(20),
    ethnicity VARCHAR(20),
    case_type VARCHAR(50),
    syndrome_type VARCHAR(50),
    address VARCHAR(200),
    district VARCHAR(100),
    discover_type VARCHAR(50),
    diagnosis VARCHAR(200),
    outcome VARCHAR(50),
    is_severe INTEGER DEFAULT 0,
    is_death INTEGER DEFAULT 0,
    risk_level VARCHAR(20),
    risk_reason TEXT,
    report_date DATE,
    hospital VARCHAR(100),
    admission_date DATE,
    discharge_date DATE,
    death_date DATE,
    fever_temp REAL,
    clinical_json TEXT,
    lab_json TEXT,
    treatment_json TEXT,
    profile_json TEXT,
    medical_record_json TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 症状信息表
CREATE TABLE IF NOT EXISTS case_symptom (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    case_id INTEGER NOT NULL,
    symptom_name VARCHAR(100) NOT NULL,
    FOREIGN KEY (case_id) REFERENCES case_info(id)
);

-- 预警模型表
CREATE TABLE IF NOT EXISTS warning_model (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    model_name VARCHAR(100) NOT NULL,
    model_type VARCHAR(50) NOT NULL,
    syndrome_type VARCHAR(50),
    config_json TEXT,
    description TEXT,
    enabled INTEGER DEFAULT 1
);

-- 预警记录表
CREATE TABLE IF NOT EXISTS warning_record (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    model_id INTEGER,
    syndrome_type VARCHAR(50),
    warning_level VARCHAR(20),
    warning_content TEXT,
    warning_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT '待研判',
    handler VARCHAR(50),
    handle_result TEXT,
    handle_time DATETIME,
    district VARCHAR(100),
    hospital VARCHAR(100),
    venue VARCHAR(200),
    observed_value REAL,
    baseline_value REAL,
    threshold_value REAL,
    anomaly_degree VARCHAR(20),
    anomaly_type VARCHAR(50),
    FOREIGN KEY (model_id) REFERENCES warning_model(id)
);

-- 预警通知表
CREATE TABLE IF NOT EXISTS warning_notification (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    warning_id INTEGER NOT NULL,
    notify_target VARCHAR(100),
    notify_method VARCHAR(50),
    notify_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    notify_status VARCHAR(20) DEFAULT '已发送',
    FOREIGN KEY (warning_id) REFERENCES warning_record(id)
);

-- 预警处置记录表
CREATE TABLE IF NOT EXISTS warning_disposal (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    warning_id INTEGER NOT NULL,
    operator VARCHAR(50),
    action_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    action_type VARCHAR(50),
    action_comment TEXT,
    attachment TEXT,
    remark TEXT,
    FOREIGN KEY (warning_id) REFERENCES warning_record(id)
);

-- 监测事件表
CREATE TABLE IF NOT EXISTS surveillance_event (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    event_name VARCHAR(200) NOT NULL,
    event_type VARCHAR(50),
    syndrome_type VARCHAR(50),
    district VARCHAR(100),
    venue VARCHAR(200),
    related_cases TEXT,
    related_warnings TEXT,
    status VARCHAR(20) DEFAULT '待核查',
    responsible_person VARCHAR(50),
    discovery_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    description TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 保存的查询条件
CREATE TABLE IF NOT EXISTS saved_query (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    query_name VARCHAR(100) NOT NULL,
    syndrome_type VARCHAR(50),
    condition_json TEXT,
    created_by VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 修改日志表
CREATE TABLE IF NOT EXISTS case_modify_log (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    case_id INTEGER NOT NULL,
    operator VARCHAR(50),
    modify_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    snapshot TEXT,
    change_desc TEXT,
    FOREIGN KEY (case_id) REFERENCES case_info(id)
);

-- 操作日志表
CREATE TABLE IF NOT EXISTS operation_log (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username VARCHAR(50),
    operation VARCHAR(200),
    ip VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 报卡信息表
CREATE TABLE IF NOT EXISTS report_card (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    case_id INTEGER NOT NULL,
    card_no VARCHAR(50),
    report_type VARCHAR(50),
    report_date DATE,
    reporter VARCHAR(50),
    status VARCHAR(20),
    FOREIGN KEY (case_id) REFERENCES case_info(id)
);
