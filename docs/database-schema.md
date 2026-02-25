-- ================================
-- RESET SCHEMA (DEV ONLY)
-- ================================
DROP SCHEMA IF EXISTS japanese_app CASCADE;
CREATE SCHEMA japanese_app;

-- =========================
-- ENUM TYPES
-- =========================

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'test_status_enum') THEN
        CREATE TYPE test_status_enum AS ENUM ('Draft', 'Published');
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'profile_status_enum') THEN
        CREATE TYPE profile_status_enum AS ENUM ('Learning', 'Pass', 'Not Pass');
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'profile_level_status_enum') THEN
        CREATE TYPE profile_level_status_enum AS ENUM ('Learning', 'Pass');
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'test_mode_enum') THEN
        CREATE TYPE test_mode_enum AS ENUM ('Practice', 'Exam');
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'test_result_status_enum') THEN
        CREATE TYPE test_result_status_enum AS ENUM ('In Progress', 'Completed');
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'report_status_enum') THEN
        CREATE TYPE report_status_enum AS ENUM ('Pending', 'Resolved');
    END IF;
END $$;

-- =========================
-- TABLES
-- =========================

-- 1. Admin
CREATE TABLE Admin (
    admin_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- 2. Learner
CREATE TABLE Learner (
    learner_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE
);

-- 3. Level
CREATE TABLE Level (
    level_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    level_name VARCHAR(50) NOT NULL,
    admin_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP,
    FOREIGN KEY (admin_id) REFERENCES Admin(admin_id) ON DELETE SET NULL
);

-- 4. Topic
CREATE TABLE Topic (
    topic_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    topic_name VARCHAR(100) NOT NULL,
    level_id INT,
    FOREIGN KEY (level_id) REFERENCES Level(level_id) ON DELETE CASCADE
);

-- 5. Vocabulary
CREATE TABLE Vocabulary (
    vocab_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    word VARCHAR(100) NOT NULL,
    meaning TEXT,
    topic_id INT,
    FOREIGN KEY (topic_id) REFERENCES Topic(topic_id) ON DELETE CASCADE
);

-- 6. AudioTest
CREATE TABLE AudioTest (
    test_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    test_name VARCHAR(100),
    topic_id INT,
    audio_url VARCHAR(255),
    duration INT,
    pass_condition INT DEFAULT 80,
    is_ai_generated BOOLEAN DEFAULT FALSE,
    status test_status_enum DEFAULT 'Draft',
    FOREIGN KEY (topic_id) REFERENCES Topic(topic_id) ON DELETE CASCADE
);

-- 7. Question
CREATE TABLE Question (
    question_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    content TEXT NOT NULL,
    test_id INT,
    FOREIGN KEY (test_id) REFERENCES AudioTest(test_id) ON DELETE CASCADE
);

-- 8. Profile
CREATE TABLE Profile (
    profile_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    learner_id INT,
    status profile_status_enum DEFAULT 'Learning',
    start_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_date TIMESTAMP,
    FOREIGN KEY (learner_id) REFERENCES Learner(learner_id) ON DELETE CASCADE
);

-- 9. Profile_Level
CREATE TABLE Profile_Level (
    profile_id INT,
    level_id INT,
    status profile_level_status_enum DEFAULT 'Learning',
    PRIMARY KEY (profile_id, level_id),
    FOREIGN KEY (profile_id) REFERENCES Profile(profile_id) ON DELETE CASCADE,
    FOREIGN KEY (level_id) REFERENCES Level(level_id) ON DELETE CASCADE
);

-- 10. Profile_Topic
CREATE TABLE Profile_Topic (
    profile_id INT,
    topic_id INT,
    status profile_level_status_enum DEFAULT 'Learning',
    PRIMARY KEY (profile_id, topic_id),
    FOREIGN KEY (profile_id) REFERENCES Profile(profile_id) ON DELETE CASCADE,
    FOREIGN KEY (topic_id) REFERENCES Topic(topic_id) ON DELETE CASCADE
);

-- 11. TestResult
CREATE TABLE TestResult (
    result_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    profile_id INT,
    topic_id INT,
    test_id INT,
    mode test_mode_enum NOT NULL,
    score INT DEFAULT 0,
    is_passed BOOLEAN DEFAULT FALSE,
    total_time INT,
    status test_result_status_enum DEFAULT 'Completed',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (profile_id, topic_id)
        REFERENCES Profile_Topic(profile_id, topic_id)
        ON DELETE CASCADE,

    FOREIGN KEY (test_id)
        REFERENCES AudioTest(test_id)
        ON DELETE CASCADE
);

-- 12. Answer
CREATE TABLE Answer (
    answer_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    question_id INT,
    content TEXT NOT NULL,
    is_correct BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (question_id) REFERENCES Question(question_id) ON DELETE CASCADE
);

-- 13. LearnerAnswer
CREATE TABLE LearnerAnswer (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    result_id INT,
    question_id INT,
    selected_answer_id INT,
    FOREIGN KEY (result_id) REFERENCES TestResult(result_id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES Question(question_id) ON DELETE CASCADE,
    FOREIGN KEY (selected_answer_id) REFERENCES Answer(answer_id)
);

-- 14. Report
CREATE TABLE Report (
    report_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    profile_id INT,
    test_id INT,
    content TEXT NOT NULL,
    status report_status_enum DEFAULT 'Pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (profile_id) REFERENCES Profile(profile_id) ON DELETE CASCADE,
    FOREIGN KEY (test_id) REFERENCES AudioTest(test_id) ON DELETE CASCADE
);