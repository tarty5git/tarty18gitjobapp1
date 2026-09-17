-- H2 In-Memory Database DDL Script for LinkedIn Job Application Bot
-- Package target: com.cth.sdm

CREATE TABLE IF NOT EXISTS job_applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_title VARCHAR(255) NOT NULL,
    company VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    job_url VARCHAR(1000) NOT NULL,
    poster_email VARCHAR(320),
    status VARCHAR(50) DEFAULT 'APPLIED' NOT NULL,
    match_score DECIMAL(5, 2),
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    posted_at TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    viewed BOOLEAN DEFAULT FALSE NOT NULL,
    notes VARCHAR(2000)
);

CREATE TABLE IF NOT EXISTS bot_execution_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    log_level VARCHAR(20) NOT NULL,
    message VARCHAR(2000) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS bot_config (
    config_key VARCHAR(100) PRIMARY KEY,
    config_value VARCHAR(1000) NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS applicant_profile (
    id BIGINT PRIMARY KEY,
    full_name VARCHAR(150),
    email VARCHAR(320),
    phone VARCHAR(50),
    location VARCHAR(150),
    linkedin_url VARCHAR(500),
    work_authorization VARCHAR(150),
    cv_filename VARCHAR(255),
    cv_content_type VARCHAR(100),
    cv_content BLOB,
    updated_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_job_app_status ON job_applications(status);
CREATE INDEX IF NOT EXISTS idx_job_app_applied_at ON job_applications(applied_at);
CREATE INDEX IF NOT EXISTS idx_job_app_posted_at ON job_applications(posted_at);
