-- Testcontainers 초기화: 레거시 DataSource용 별도 스키마 생성
CREATE DATABASE IF NOT EXISTS luxurydb_test
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
