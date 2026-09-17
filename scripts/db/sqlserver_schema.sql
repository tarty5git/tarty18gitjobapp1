-- Microsoft SQL Server Database DDL Script for LinkedIn Job Application Bot
-- Package target: com.cth.sdm

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'job_applications')
BEGIN
    CREATE TABLE job_applications (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        job_title NVARCHAR(255) NOT NULL,
        company NVARCHAR(255) NOT NULL,
        location NVARCHAR(255),
        job_url NVARCHAR(1000) NOT NULL,
        poster_email NVARCHAR(320),
        status NVARCHAR(50) DEFAULT 'APPLIED' NOT NULL,
        match_score DECIMAL(5, 2),
        applied_at DATETIME2 DEFAULT GETDATE() NOT NULL,
        posted_at DATETIME2,
        updated_at DATETIME2 DEFAULT GETDATE(),
        viewed BIT DEFAULT 0 NOT NULL,
        notes NVARCHAR(2000)
    );
END;

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'bot_execution_logs')
BEGIN
    CREATE TABLE bot_execution_logs (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        log_level NVARCHAR(20) NOT NULL,
        message NVARCHAR(2000) NOT NULL,
        created_at DATETIME2 DEFAULT GETDATE() NOT NULL
    );
END;

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'bot_config')
BEGIN
    CREATE TABLE bot_config (
        config_key NVARCHAR(100) PRIMARY KEY,
        config_value NVARCHAR(1000) NOT NULL,
        updated_at DATETIME2 DEFAULT GETDATE()
    );
END;

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'applicant_profile')
BEGIN
    CREATE TABLE applicant_profile (
        id BIGINT PRIMARY KEY,
        full_name NVARCHAR(150),
        email NVARCHAR(320),
        phone NVARCHAR(50),
        location NVARCHAR(150),
        linkedin_url NVARCHAR(500),
        work_authorization NVARCHAR(150),
        cv_filename NVARCHAR(255),
        cv_content_type NVARCHAR(100),
        cv_content VARBINARY(MAX),
        updated_at DATETIME2
    );
END;

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_job_app_status')
    CREATE INDEX idx_job_app_status ON job_applications(status);

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_job_app_applied_at')
    CREATE INDEX idx_job_app_applied_at ON job_applications(applied_at);

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_job_app_posted_at')
    CREATE INDEX idx_job_app_posted_at ON job_applications(posted_at);
