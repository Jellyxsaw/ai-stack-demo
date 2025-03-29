-- 創建用戶表
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);

-- 創建 GPU 庫存表
CREATE TABLE IF NOT EXISTS gpu_inventory (
    id SERIAL PRIMARY KEY,
    gpu_model_id VARCHAR(50) NOT NULL,
    quantity INT NOT NULL,
    hardware_id BIGINT
);

-- 創建客戶硬體表
CREATE TABLE IF NOT EXISTS customer_hardware (
    id SERIAL PRIMARY KEY,
    customer_id VARCHAR(50) NOT NULL
);

-- 創建計算任務表
CREATE TABLE IF NOT EXISTS compute_jobs (
    id SERIAL PRIMARY KEY,
    job_id VARCHAR(50) NOT NULL,
    customer_id VARCHAR(50) NOT NULL,
    job_type VARCHAR(20) NOT NULL,
    data_size_gb DOUBLE PRECISION NOT NULL,
    expected_duration_hours DOUBLE PRECISION NOT NULL,
    precision VARCHAR(20) NOT NULL,
    parallelism VARCHAR(20) NOT NULL
);

-- 創建用戶配置表
CREATE TABLE IF NOT EXISTS user_configurations (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    config_name VARCHAR(100) NOT NULL,
    hardware_id BIGINT,
    job_id BIGINT,
    FOREIGN KEY (hardware_id) REFERENCES customer_hardware(id) ON DELETE CASCADE,
    FOREIGN KEY (job_id) REFERENCES compute_jobs(id) ON DELETE CASCADE,
    CONSTRAINT unique_user_config UNIQUE (username, config_name)
);

-- 插入預設管理員帳號 (密碼為加密後的 'admin')
INSERT INTO users (username, password, role) 
VALUES ('admin', '$2a$10$ixlPY3AAd4ty1l6E2IsQ9OFZi2ba9ZQE0bP7RFcGIWNip/Lna6JwC', 'ADMIN')
ON CONFLICT (username) DO NOTHING;

-- 插入預設用戶帳號 (密碼為加密後的 'user')
INSERT INTO users (username, password, role) 
VALUES ('user', '$2a$10$teJrCEnsxNT49ZpXU7n22O27aCGbVYYe/RG6/XxdWPJbOLZYf.9Vy', 'USER')
ON CONFLICT (username) DO NOTHING;
