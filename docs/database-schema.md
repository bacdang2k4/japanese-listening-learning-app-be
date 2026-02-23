-- 1. Tạo Database
CREATE DATABASE IF NOT EXISTS JapaneseLearningApp;
USE JapaneseLearningApp;

-- 2. Bảng Admin
CREATE TABLE Admin (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- 3. Bảng Learner (Tài khoản người dùng)
CREATE TABLE Learner (
    learner_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE
);

-- 4. Bảng Level
CREATE TABLE Level (
    level_id INT AUTO_INCREMENT PRIMARY KEY,
    level_name VARCHAR(50) NOT NULL,
    admin_id INT,
    FOREIGN KEY (admin_id) REFERENCES Admin(admin_id) ON DELETE SET NULL
);

-- 5. Bảng Topic
CREATE TABLE Topic (
    topic_id INT AUTO_INCREMENT PRIMARY KEY,
    topic_name VARCHAR(100) NOT NULL,
    level_id INT,
    FOREIGN KEY (level_id) REFERENCES Level(level_id) ON DELETE CASCADE
);

-- 6. Bảng Vocabank (Từ vựng)
CREATE TABLE Vocabulary (
    vocab_id INT AUTO_INCREMENT PRIMARY KEY,
    word VARCHAR(100) NOT NULL,
    meaning TEXT,
    topic_id INT,
    FOREIGN KEY (topic_id) REFERENCES Topic(topic_id) ON DELETE CASCADE
);

-- 7. Bảng Bài Test (AudioTest) - Cập nhật status, AI, duration
CREATE TABLE AudioTest (
    test_id INT AUTO_INCREMENT PRIMARY KEY,
    test_name VARCHAR(100),
    topic_id INT,
    audio_url VARCHAR(255),
    duration INT, -- Độ dài audio (giây)
    pass_condition INT DEFAULT 80,
    is_ai_generated BOOLEAN DEFAULT FALSE, -- Đánh dấu nội dung do AI tạo
    status ENUM('Draft', 'Published') DEFAULT 'Draft', -- Trạng thái bài test
    FOREIGN KEY (topic_id) REFERENCES Topic(topic_id) ON DELETE CASCADE
);

-- 8. Bảng Câu hỏi
CREATE TABLE Question (
    question_id INT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    test_id INT,
    FOREIGN KEY (test_id) REFERENCES AudioTest(test_id) ON DELETE CASCADE
);

-- 9. Bảng Profile (Hồ sơ học tập)
CREATE TABLE Profile (
    profile_id INT AUTO_INCREMENT PRIMARY KEY,
    learner_id INT,
    status ENUM('Learning', 'Pass', 'Not Pass') DEFAULT 'Learning',
    start_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    end_date DATETIME,
    FOREIGN KEY (learner_id) REFERENCES Learner(learner_id) ON DELETE CASCADE
);

-- 10. Bảng trung gian Profile_Level
CREATE TABLE Profile_Level (
    profile_id INT,
    level_id INT,
    status ENUM('Learning', 'Pass') DEFAULT 'Learning',
    PRIMARY KEY (profile_id, level_id),
    FOREIGN KEY (profile_id) REFERENCES Profile(profile_id) ON DELETE CASCADE,
    FOREIGN KEY (level_id) REFERENCES Level(level_id) ON DELETE CASCADE
);

-- 11. Bảng trung gian Profile_Topic
CREATE TABLE Profile_Topic (
    profile_id INT,
    topic_id INT,
    status ENUM('Learning', 'Pass') DEFAULT 'Learning',
    PRIMARY KEY (profile_id, topic_id),
    FOREIGN KEY (profile_id) REFERENCES Profile(profile_id) ON DELETE CASCADE,
    FOREIGN KEY (topic_id) REFERENCES Topic(topic_id) ON DELETE CASCADE
);

-- 12. Bảng Kết quả bài test - Cập nhật status, total_time
CREATE TABLE TestResult (
    result_id INT AUTO_INCREMENT PRIMARY KEY,
    profile_id INT,
    topic_id INT,
    test_id INT,
    mode ENUM('Practice', 'Exam') NOT NULL,
    score INT DEFAULT 0,
    is_passed BOOLEAN DEFAULT FALSE,
    total_time INT, -- Thời gian làm bài thực tế (giây)
    status ENUM('In_Progress', 'Completed') DEFAULT 'Completed', -- Trạng thái bài làm
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (profile_id, topic_id)
        REFERENCES Profile_Topic(profile_id, topic_id)
        ON DELETE CASCADE,

    FOREIGN KEY (test_id)
        REFERENCES AudioTest(test_id)
        ON DELETE CASCADE
);

-- 13. Bảng câu trả lời
CREATE TABLE Answer (
    answer_id INT AUTO_INCREMENT PRIMARY KEY,
    question_id INT,
    content TEXT NOT NULL,
    is_correct BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (question_id) REFERENCES Question(question_id) ON DELETE CASCADE
);

-- 14. Bảng Chi tiết câu trả lời của Learner
CREATE TABLE LearnerAnswer (
    id INT AUTO_INCREMENT PRIMARY KEY,
    result_id INT,
    question_id INT,
    selected_answer_id INT,
    FOREIGN KEY (result_id) REFERENCES TestResult(result_id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES Question(question_id) ON DELETE CASCADE,
    FOREIGN KEY (selected_answer_id) REFERENCES Answer(answer_id)
);

-- 15. Bảng Reports (Đã đổi learner_id thành profile_id)
CREATE TABLE Report (
    report_id INT AUTO_INCREMENT PRIMARY KEY,
    profile_id INT, -- Quan hệ qua Profile để biết bối cảnh lúc báo cáo lỗi
    test_id INT,
    content TEXT NOT NULL,
    status ENUM('Pending', 'Resolved') DEFAULT 'Pending',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (profile_id) REFERENCES Profile(profile_id) ON DELETE CASCADE,
    FOREIGN KEY (test_id) REFERENCES AudioTest(test_id) ON DELETE CASCADE
);
