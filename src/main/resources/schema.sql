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
    status VARCHAR(20) DEFAULT '待处置',
    handler VARCHAR(50),
    handle_result TEXT,
    handle_time DATETIME,
    FOREIGN KEY (model_id) REFERENCES warning_model(id)
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
