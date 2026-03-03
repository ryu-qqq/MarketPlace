package com.ryuqq.marketplace.application.legacy.session.dto.response;

/**
 * 레거시 Presigned URL 발급 결과.
 *
 * <p>세토프 어드민의 PreSignedUrlResponse와 호환됩니다.
 *
 * @param sessionId 업로드 세션 ID (업로드 완료 처리에 필요)
 * @param preSignedUrl S3 Presigned URL
 * @param objectKey S3 객체 키
 */
public record LegacyPresignedUrlResult(String sessionId, String preSignedUrl, String objectKey) {}
