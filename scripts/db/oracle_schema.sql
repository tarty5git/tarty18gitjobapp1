-- Oracle Database DDL Script for LinkedIn Job Application Bot
-- Package target: com.cth.sdm

CREATE TABLE job_applications (
    id NUMBER(19, 0) GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    job_title VARCHAR2(255) NOT NULL,
    company VARCHAR2(255) NOT NULL,
    location VARCHAR2(255),
    job_url VARCHAR2(1000) NOT NULL,
    poster_email VARCHAR2(320),
    status VARCHAR2(50) DEFAULT 'APPLIED' NOT NULL,
    match_score NUMBER(5, 2),
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    posted_at TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    viewed NUMBER(1) DEFAULT 0 NOT NULL,
    notes VARCHAR2(2000)
);

CREATE TABLE bot_execution_logs (
    id NUMBER(19, 0) GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    log_level VARCHAR2(20) NOT NULL,
    message VARCHAR2(2000) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE bot_config (
    config_key VARCHAR2(100) PRIMARY KEY,
    config_value VARCHAR2(1000) NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE applicant_profile (
    id NUMBER(19, 0) PRIMARY KEY,
    full_name VARCHAR2(150),
    email VARCHAR2(320),
    phone VARCHAR2(50),
    location VARCHAR2(150),
    linkedin_url VARCHAR2(500),
    work_authorization VARCHAR2(150),
    cv_filename VARCHAR2(255),
    cv_content_type VARCHAR2(100),
    cv_content BLOB,
    updated_at TIMESTAMP
);

CREATE INDEX idx_job_app_status ON job_applications(status);
CREATE INDEX idx_job_app_applied_at ON job_applications(applied_at);
CREATE INDEX idx_job_app_posted_at ON job_applications(posted_at);
