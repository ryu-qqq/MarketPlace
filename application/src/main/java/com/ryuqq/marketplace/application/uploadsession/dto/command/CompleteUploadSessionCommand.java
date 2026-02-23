package com.ryuqq.marketplace.application.uploadsession.dto.command;

/**
 * 업로드 세션 완료 처리 커맨드.
 *
 * @param sessionId 업로드 세션 ID
 * @param fileSize 업로드된 파일 크기 (바이트)
 * @param etag S3 ETag (nullable)
 */
public record CompleteUploadSessionCommand(String sessionId, long fileSize, String etag) {}
