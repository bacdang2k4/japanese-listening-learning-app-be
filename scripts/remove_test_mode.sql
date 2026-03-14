-- Migration: Remove test mode (PRACTICE/EXAM) - run before deploying backend
-- PostgreSQL

ALTER TABLE testattempt DROP COLUMN IF EXISTS mode;
DROP TYPE IF EXISTS test_mode_enum;
