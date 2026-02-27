SELECT n.nspname as schema, t.typname as type
FROM pg_type t
JOIN pg_enum e ON t.oid = e.enumtypid
JOIN pg_namespace n ON n.oid = t.typnamespace
GROUP BY n.nspname, t.typname;

DO $$ 
DECLARE 
    r RECORD;
BEGIN
    FOR r IN (
        SELECT n.nspname, t.typname 
        FROM pg_type t 
        JOIN pg_enum e ON t.oid = e.enumtypid 
        JOIN pg_namespace n ON n.oid = t.typnamespace
        WHERE n.nspname = 'public' -- Chỉ xóa trong schema public
        GROUP BY n.nspname, t.typname
    ) LOOP
        EXECUTE 'DROP TYPE ' || quote_ident(r.nspname) || '.' || quote_ident(r.typname) || ' CASCADE';
    END LOOP;
END $$;
-- =========================
-- ENUM TYPES
-- =========================

CREATE TYPE test_status_enum AS ENUM (
    'DRAFT',
    'GENERATING',
    'PENDING_REVIEW',
    'PUBLISHED',
    'REJECTED'
);

CREATE TYPE profile_status_enum AS ENUM (
    'LEARNING',
    'PASS',
    'NOT_PASS'
);

CREATE TYPE profile_level_status_enum AS ENUM (
    'LEARNING',
    'PASS'
);

CREATE TYPE test_mode_enum AS ENUM (
    'PRACTICE',
    'EXAM'
);

CREATE TYPE test_result_status_enum AS ENUM (
    'IN_PROGRESS',
    'COMPLETED'
);

CREATE TYPE report_status_enum AS ENUM (
    'PENDING',
    'RESOLVED'
);

CREATE TYPE generated_status_enum AS ENUM (
    'PENDING',
    'APPROVED',
    'REJECTED'
);

-- =========================
-- TABLES
-- =========================

-- 1. Admin
CREATE TABLE Admin (
    admin_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Learner
CREATE TABLE Learner (
    learner_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Level
CREATE TABLE Level (
    level_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    level_name VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- 4. Topic
CREATE TABLE Topic (
    topic_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    topic_name VARCHAR(100) NOT NULL,
    level_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (level_id) REFERENCES Level(level_id) ON DELETE CASCADE
);

-- 5. Vocabulary
CREATE TABLE Vocabulary (
    vocab_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    word VARCHAR(100) NOT NULL,
    meaning TEXT,
    topic_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (topic_id) REFERENCES Topic(topic_id) ON DELETE CASCADE
);

-- 6. AudioTest
CREATE TABLE AudioTest (
    test_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    test_name VARCHAR(100),
    topic_id INT NOT NULL,
    audio_url VARCHAR(255),
    duration INT,
    pass_condition INT DEFAULT 80,
    is_ai_generated BOOLEAN DEFAULT FALSE,
    ai_prompt TEXT,
    ai_model VARCHAR(100),
    ai_generated_at TIMESTAMP,
    ai_version INT DEFAULT 1,
    status test_status_enum DEFAULT 'DRAFT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (topic_id) REFERENCES Topic(topic_id) ON DELETE CASCADE
);

-- 7. Question
CREATE TABLE Question (
    question_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    content TEXT NOT NULL,
    test_id INT NOT NULL,
    audio_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (test_id) REFERENCES AudioTest(test_id) ON DELETE CASCADE
);

-- 8. Answer
CREATE TABLE Answer (
    answer_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    question_id INT NOT NULL,
    content TEXT NOT NULL,
    is_correct BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (question_id) REFERENCES Question(question_id) ON DELETE CASCADE
);

-- 9. GeneratedQuestion (AI moderation layer)
CREATE TABLE GeneratedQuestion (
    generated_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    test_id INT NOT NULL,
    content TEXT NOT NULL,
    status generated_status_enum DEFAULT 'PENDING',
    ai_prompt TEXT,
    ai_model VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (test_id) REFERENCES AudioTest(test_id) ON DELETE CASCADE
);

-- 10. Profile
CREATE TABLE Profile (
    profile_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    learner_id INT NOT NULL,
    status profile_status_enum DEFAULT 'LEARNING',
    start_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_date TIMESTAMP,
    FOREIGN KEY (learner_id) REFERENCES Learner(learner_id) ON DELETE CASCADE
);

-- 11. Profile_Level
CREATE TABLE Profile_Level (
    profile_id INT,
    level_id INT,
    status profile_level_status_enum DEFAULT 'LEARNING',
    PRIMARY KEY (profile_id, level_id),
    FOREIGN KEY (profile_id) REFERENCES Profile(profile_id) ON DELETE CASCADE,
    FOREIGN KEY (level_id) REFERENCES Level(level_id) ON DELETE CASCADE
);

-- 12. Profile_Topic
CREATE TABLE Profile_Topic (
    profile_id INT,
    topic_id INT,
    status profile_level_status_enum DEFAULT 'LEARNING',
    PRIMARY KEY (profile_id, topic_id),
    FOREIGN KEY (profile_id) REFERENCES Profile(profile_id) ON DELETE CASCADE,
    FOREIGN KEY (topic_id) REFERENCES Topic(topic_id) ON DELETE CASCADE
);

-- 13. TestResult
CREATE TABLE TestResult (
    result_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    profile_id INT NOT NULL,
    topic_id INT NOT NULL,
    test_id INT NOT NULL,
    mode test_mode_enum NOT NULL,
    score INT DEFAULT 0,
    is_passed BOOLEAN DEFAULT FALSE,
    total_time INT,
    status test_result_status_enum DEFAULT 'IN_PROGRESS',
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (profile_id, topic_id)
        REFERENCES Profile_Topic(profile_id, topic_id)
        ON DELETE CASCADE,

    FOREIGN KEY (test_id)
        REFERENCES AudioTest(test_id)
        ON DELETE CASCADE
);

-- 14. LearnerAnswer
CREATE TABLE LearnerAnswer (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    result_id INT NOT NULL,
    question_id INT NOT NULL,
    selected_answer_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (result_id) REFERENCES TestResult(result_id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES Question(question_id) ON DELETE CASCADE,
    FOREIGN KEY (selected_answer_id) REFERENCES Answer(answer_id),

    CONSTRAINT uq_result_question UNIQUE (result_id, question_id)
);

-- 15. Report
CREATE TABLE Report (
    report_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    profile_id INT NOT NULL,
    test_id INT NOT NULL,
    content TEXT NOT NULL,
    status report_status_enum DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (profile_id) REFERENCES Profile(profile_id) ON DELETE CASCADE,
    FOREIGN KEY (test_id) REFERENCES AudioTest(test_id) ON DELETE CASCADE
);

-- =========================
-- INDEXES (Performance)
-- =========================

CREATE INDEX idx_testresult_profile ON TestResult(profile_id);
CREATE INDEX idx_testresult_test ON TestResult(test_id);
CREATE INDEX idx_question_test ON Question(test_id);
CREATE INDEX idx_answer_question ON Answer(question_id);

ALTER TABLE Level
ADD COLUMN created_by_admin_id INT NOT NULL;

ALTER TABLE Level
ADD CONSTRAINT fk_level_admin
FOREIGN KEY (created_by_admin_id)
REFERENCES Admin(admin_id)
ON DELETE RESTRICT;