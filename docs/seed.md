USE JapaneseLearningApp;

-- =========================
-- 1. Admin
-- =========================
INSERT INTO Admins (username, password)
VALUES ('admin', 'admin_password_hash');

-- =========================
-- 2. Learners
-- =========================
INSERT INTO Learners (username, password, email)
VALUES
('learner1', 'password1_hash', 'learner1@gmail.com'),
('learner2', 'password2_hash', 'learner2@gmail.com');

-- =========================
-- 3. Levels
-- =========================
INSERT INTO Levels (level_name, admin_id)
VALUES
('N5', 1),
('N4', 1);

-- =========================
-- 4. Topics
-- =========================
INSERT INTO Topics (topic_name, level_id)
VALUES
('Greetings', 1),
('Daily Activities', 1),
('Travel', 2);

-- =========================
-- 5. Vocabularies
-- =========================
INSERT INTO Vocabularies (word, meaning, topic_id)
VALUES
('こんにちは', 'Hello', 1),
('ありがとう', 'Thank you', 1),
('行きます', 'Go', 2),
('食べます', 'Eat', 2),
('空港', 'Airport', 3);

-- =========================
-- 6. Audio Tests
-- =========================
INSERT INTO AudioTests (test_name, topic_id, audio_url, pass_condition)
VALUES
('Greeting Listening Test', 1, 'audio/greetings.mp3', 80),
('Daily Activity Test', 2, 'audio/daily.mp3', 80);

-- =========================
-- 7. Questions
-- =========================
INSERT INTO Questions (content, test_id)
VALUES
('「こんにちは」 có nghĩa là gì?', 1),
('「ありがとう」 có nghĩa là gì?', 1),
('「行きます」 có nghĩa là gì?', 2);

-- =========================
-- 8. Answers
-- =========================
-- Question 1
INSERT INTO Answers (question_id, content, is_correct)
VALUES
(1, 'Hello', TRUE),
(1, 'Goodbye', FALSE),
(1, 'Thank you', FALSE);

-- Question 2
INSERT INTO Answers (question_id, content, is_correct)
VALUES
(2, 'Thank you', TRUE),
(2, 'Sorry', FALSE),
(2, 'Hello', FALSE);

-- Question 3
INSERT INTO Answers (question_id, content, is_correct)
VALUES
(3, 'Go', TRUE),
(3, 'Eat', FALSE),
(3, 'Drink', FALSE);

-- =========================
-- 9. Profiles
-- =========================
INSERT INTO Profiles (learner_id, status)
VALUES
(1, 'Learning'),
(2, 'Learning');

-- =========================
-- 10. Profile_Levels
-- =========================
INSERT INTO Profile_Levels (profile_id, level_id, status)
VALUES
(1, 1, 'Learning'),
(2, 1, 'Pass');

-- =========================
-- 11. Profile_Topics
-- =========================
INSERT INTO Profile_Topics (profile_id, topic_id, status)
VALUES
(1, 1, 'Learning'),
(1, 2, 'Learning'),
(2, 1, 'Pass');

-- =========================
-- 12. Test Results
-- =========================
INSERT INTO TestResults (profile_id, topic_id, test_id, mode, score, is_passed)
VALUES
(1, 1, 1, 'Practice', 70, FALSE),
(1, 1, 1, 'Exam', 85, TRUE);

-- =========================
-- 13. Learner Answers
-- =========================
INSERT INTO LearnerAnswers (result_id, question_id, selected_answer_id)
VALUES
(2, 1, 1), -- Hello (correct)
(2, 2, 4), -- Thank you (correct)
(2, 3, 7); -- Go (correct)
