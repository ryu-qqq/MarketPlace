package com.ryuqq.marketplace.application.imageupload.dto.command;

/**
 * 이미지 업로드 콜백 완료 커맨드.
 *
 * @param downloadTaskId FileFlow 다운로드 태스크 ID
 * @param assetId FileFlow 에셋 ID
 * @param s3Key S3 저장 경로
 * @param status 태스크 상태 (COMPLETED, FAILED)
 * @param lastError 에러 메시지 (FAILED 시)
 */
public record CompleteImageUploadCallbackCommand(
        String downloadTaskId, String assetId, String s3Key, String status, String lastError) {}
