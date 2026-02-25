-- ==========================
-- 1. ADMIN DATA
-- ==========================
INSERT INTO Admin (username, password) VALUES
('admin1', 'hashed_password_1'),
('admin2', 'hashed_password_2');


-- ==========================
-- 2. LEARNER DATA
-- ==========================
INSERT INTO Learner (username, password, email) VALUES
('hikari', 'pass123', 'hikari@gmail.com'),
('taro', 'pass123', 'taro@gmail.com'),
('sakura', 'pass123', 'sakura@gmail.com'),
('yuki', 'pass123', 'yuki@gmail.com'),
('kenji', 'pass123', 'kenji@gmail.com');


-- ==========================
-- 3. LEVEL DATA
-- ==========================
INSERT INTO Level (level_name, admin_id) VALUES
('N5', 1),
('N4', 1),
('N3', 2),
('N2', 2);


-- ==========================
-- 4. TOPIC DATA
-- ==========================
INSERT INTO Topic (topic_name, level_id) VALUES
('Greetings', 1),
('Daily Life', 1),
('School', 1),
('Travel', 2),
('Shopping', 2),
('Workplace', 3),
('Business Email', 4);


-- ==========================
-- 5. VOCABULARY DATA
-- ==========================
INSERT INTO Vocabulary (word, meaning, topic_id) VALUES
('こんにちは', 'Hello', 1),
('ありがとう', 'Thank you', 1),
('さようなら', 'Goodbye', 1),
('学校', 'School', 3),
('学生', 'Student', 3),
('電車', 'Train', 4),
('切符', 'Ticket', 4),
('買い物', 'Shopping', 5),
('会社', 'Company', 6),
('会議', 'Meeting', 6),
('メール', 'Email', 7);


-- ==========================
-- 6. AUDIO TEST DATA
-- ==========================
INSERT INTO AudioTest 
(test_name, topic_id, audio_url, duration, pass_condition, is_ai_generated, status)
VALUES
('Basic Greeting Test', 1, 'audio/greeting1.mp3', 120, 80, FALSE, 'Published'),
('Daily Conversation Test', 2, 'audio/daily1.mp3', 150, 75, TRUE, 'Published'),
('School Dialogue Test', 3, 'audio/school1.mp3', 180, 70, FALSE, 'Published'),
('Travel Situation Test', 4, 'audio/travel1.mp3', 200, 70, TRUE, 'Draft'),
('Business Listening Test', 6, 'audio/business1.mp3', 240, 85, FALSE, 'Published');


-- ==========================
-- 7. QUESTION DATA
-- ==========================
INSERT INTO Question (content, test_id) VALUES
('What does こんにちは mean?', 1),
('How to say goodbye in Japanese?', 1),
('Where is the train station?', 4),
('Meaning of 学校?', 3),
('What is 会社?', 5),
('What does ありがとう mean?', 2);


-- ==========================
-- 8. ANSWER DATA
-- ==========================
INSERT INTO Answer (question_id, content, is_correct) VALUES
(1, 'Hello', TRUE),
(1, 'Thank you', FALSE),
(1, 'Goodbye', FALSE),

(2, 'さようなら', TRUE),
(2, 'こんにちは', FALSE),

(3, '電車はどこですか', TRUE),
(3, '学校はどこですか', FALSE),

(4, 'School', TRUE),
(4, 'Company', FALSE),

(5, 'Company', TRUE),
(5, 'Meeting', FALSE),

(6, 'Thank you', TRUE),
(6, 'Sorry', FALSE);


-- ==========================
-- 9. PROFILE DATA
-- ==========================
INSERT INTO Profile (learner_id, status, end_date) VALUES
(1, 'Learning', NULL),
(2, 'Pass', '2025-01-10'),
(3, 'Not Pass', '2025-01-15'),
(4, 'Learning', NULL),
(5, 'Learning', NULL);


-- ==========================
-- 10. PROFILE LEVEL DATA
-- ==========================
INSERT INTO Profile_Level (profile_id, level_id, status) VALUES
(1, 1, 'Learning'),
(2, 1, 'Pass'),
(3, 2, 'Learning'),
(4, 3, 'Learning'),
(5, 1, 'Learning');


-- ==========================
-- 11. PROFILE TOPIC DATA
-- ==========================
INSERT INTO Profile_Topic (profile_id, topic_id, status) VALUES
(1, 1, 'Learning'),
(1, 2, 'Learning'),
(2, 1, 'Pass'),
(3, 4, 'Learning'),
(4, 6, 'Learning'),
(5, 3, 'Learning');


-- ==========================
-- 12. TEST RESULT DATA
-- ==========================
INSERT INTO TestResult 
(profile_id, topic_id, test_id, mode, score, is_passed, total_time, status)
VALUES
(1, 1, 1, 'Practice', 85, TRUE, 110, 'Completed'),
(2, 1, 1, 'Exam', 90, TRUE, 95, 'Completed'),
(3, 4, 4, 'Practice', 60, FALSE, 150, 'Completed'),
(4, 6, 5, 'Exam', 88, TRUE, 200, 'Completed'),
(5, 3, 3, 'Practice', 72, TRUE, 175, 'Completed');


-- ==========================
-- 13. LEARNER ANSWER DATA
-- ==========================
INSERT INTO LearnerAnswer (result_id, question_id, selected_answer_id) VALUES
(1, 1, 1),
(1, 2, 4),
(2, 1, 1),
(2, 2, 4),
(3, 3, 7),
(4, 5, 10),
(5, 4, 8);


-- ==========================
-- 14. REPORT DATA
-- ==========================
INSERT INTO Report (profile_id, test_id, content, status) VALUES
(1, 1, 'Audio is too fast', 'Pending'),
(2, 1, 'Answer option seems incorrect', 'Resolved'),
(3, 4, 'Sound quality is bad', 'Pending'),
(4, 5, 'Need more explanation', 'Pending');