-- 1. Tạo Database
CREATE DATABASE IF NOT EXISTS JapaneseLearningApp;
USE JapaneseLearningApp;

-- 2. Bảng Admin
CREATE TABLE Admins (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- 3. Bảng Learner (Người dùng)
CREATE TABLE Learners (
    learner_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE
);

-- 4. Bảng Level
CREATE TABLE Levels (
    level_id INT AUTO_INCREMENT PRIMARY KEY,
    level_name VARCHAR(50) NOT NULL,
    admin_id INT,
    FOREIGN KEY (admin_id) REFERENCES Admins(admin_id) ON DELETE SET NULL
);

-- 5. Bảng Topic
CREATE TABLE Topics (
    topic_id INT AUTO_INCREMENT PRIMARY KEY,
    topic_name VARCHAR(100) NOT NULL,
    level_id INT,
    FOREIGN KEY (level_id) REFERENCES Levels(level_id) ON DELETE CASCADE
);

-- 6. Bảng Vocabank (Từ vựng)
CREATE TABLE Vocabularies (
    vocab_id INT AUTO_INCREMENT PRIMARY KEY,
    word VARCHAR(100) NOT NULL,
    meaning TEXT,
    topic_id INT,
    FOREIGN KEY (topic_id) REFERENCES Topics(topic_id) ON DELETE CASCADE
);

-- 7. Bảng Bài Test (AudioTest)
CREATE TABLE AudioTests (
    test_id INT AUTO_INCREMENT PRIMARY KEY,
    test_name VARCHAR(100),
    topic_id INT,
    audio_url VARCHAR(255),
    pass_condition INT default 80,	
    FOREIGN KEY (topic_id) REFERENCES Topics(topic_id) ON DELETE CASCADE
);

-- 8. Bảng Câu hỏi
CREATE TABLE Questions (
    question_id INT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    test_id INT,
    FOREIGN KEY (test_id) REFERENCES AudioTests(test_id) ON DELETE CASCADE
);


-- 10. Bảng Profile (Hồ sơ học tập)
CREATE TABLE Profiles (
    profile_id INT AUTO_INCREMENT PRIMARY KEY,
    learner_id INT,
    status ENUM('Learning', 'Pass', 'Not Pass') DEFAULT 'Learning',
    start_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    end_date DATETIME,
    FOREIGN KEY (learner_id) REFERENCES Learners(learner_id) ON DELETE CASCADE
);

-- 11. Bảng trung gian Profile_Levels
CREATE TABLE Profile_Levels (
    profile_id INT,
    level_id INT,
    status ENUM('Learning', 'Pass') DEFAULT 'Learning',
    PRIMARY KEY (profile_id, level_id),
    FOREIGN KEY (profile_id) REFERENCES Profiles(profile_id) ON DELETE CASCADE,
    FOREIGN KEY (level_id) REFERENCES Levels(level_id) ON DELETE CASCADE
);

-- 12. Bảng trung gian Profile_Topics
CREATE TABLE Profile_Topics (
    profile_id INT,
    topic_id INT,
    status ENUM('Learning', 'Pass') DEFAULT 'Learning',
    PRIMARY KEY (profile_id, topic_id),
    FOREIGN KEY (profile_id) REFERENCES Profiles(profile_id) ON DELETE CASCADE,
    FOREIGN KEY (topic_id) REFERENCES Topics(topic_id) ON DELETE CASCADE
);

-- 13. Bảng Kết quả bài test
CREATE TABLE TestResults (
    result_id INT AUTO_INCREMENT PRIMARY KEY,

    profile_id INT,
    topic_id INT,
    test_id INT,

    mode ENUM('Practice', 'Exam') NOT NULL,
    score INT DEFAULT 0,
    is_passed BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    -- FK tới bảng trung gian Profile_Topics
    FOREIGN KEY (profile_id, topic_id)
        REFERENCES Profile_Topics(profile_id, topic_id)
        ON DELETE CASCADE,

    FOREIGN KEY (test_id)
        REFERENCES AudioTests(test_id)
        ON DELETE CASCADE
);


-- 14. Bảng câu trả lời
CREATE TABLE Answers (
    answer_id INT AUTO_INCREMENT PRIMARY KEY,
    question_id INT,
    content TEXT NOT NULL,
    is_correct BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (question_id) REFERENCES Questions(question_id) ON DELETE CASCADE
);

-- 15. Bảng Chi tiết câu trả lời của Learner
CREATE TABLE LearnerAnswers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    result_id INT,
    question_id INT,
    selected_answer_id INT,
    FOREIGN KEY (result_id) REFERENCES TestResults(result_id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES Questions(question_id) ON DELETE CASCADE
);


